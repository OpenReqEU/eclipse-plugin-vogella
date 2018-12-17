
package com.vogella.prioritizer.ui.parts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.TextDecorationEnum;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.common.core.service.BugzillaService;
import com.vogella.common.ui.util.WidgetUtils;
import com.vogella.prioritizer.core.events.Events;
import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.preferences.Preferences;
import com.vogella.prioritizer.core.service.BrowserService;
import com.vogella.prioritizer.core.service.PrioritizerService;
import com.vogella.prioritizer.ui.nattable.BugColumnPropertyAccessor;
import com.vogella.prioritizer.ui.nattable.BugHeaderDataProvider;
import com.vogella.prioritizer.ui.nattable.LinkClickConfiguration;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.swing.SwtScheduler;

@SuppressWarnings("restriction")
public class MostDiscussedBugsOfTheMonthPart {

	private static final Logger LOG = LoggerFactory.getLogger(MostDiscussedBugsOfTheMonthPart.class);

	@Inject
	@Preference
	private IEclipsePreferences preferences;

	@Inject
	private PrioritizerService prioritizerService;

	@Inject
	private BugzillaService bugzillaService;

	@Inject
	private BrowserService browserService;

	private StackLayout stackLayout;

	private Composite settingsComposite;

	private Composite mainComposite;

	private ViewType currentViewType = ViewType.MAIN;

	private EventList<Bug> eventList;

	private NatTable natTable;

	private Disposable mostDiscussedBugQuery;

	private ResourceManager resourceManager;

	@PostConstruct
	public void createPartControl(Composite parent) {
		resourceManager = new LocalResourceManager(JFaceResources.getResources(), parent);

		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		createNatTable(parent);

		createSettings(parent);
	}

	private void createNatTable(Composite parent) {

		mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(mainComposite);

		stackLayout.topControl = mainComposite;

		eventList = new BasicEventList<>(10);

		IColumnPropertyAccessor<Bug> bugColumnPropertyAccessor = new BugColumnPropertyAccessor();

		ListDataProvider<Bug> dataProvider = new ListDataProvider<>(eventList, bugColumnPropertyAccessor);
		DataLayer dataLayer = new DataLayer(dataProvider);
		dataLayer.setColumnPercentageSizing(true);
		dataLayer.setColumnWidthPercentageByPosition(0, 7);
		dataLayer.setColumnWidthPercentageByPosition(1, 60);
		dataLayer.setColumnWidthPercentageByPosition(2, 11);
		dataLayer.setColumnWidthPercentageByPosition(3, 11);
		dataLayer.setColumnWidthPercentageByPosition(4, 11);
		ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(dataLayer);
		ColumnLabelAccumulator columnLabelAccumulator = new ColumnLabelAccumulator(dataProvider);
		dataLayer.setConfigLabelAccumulator(columnLabelAccumulator);

		SelectionLayer selectionLayer = new SelectionLayer(columnReorderLayer, false);

		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		IDataProvider headerDataProvider = new BugHeaderDataProvider();
		DataLayer headerDataLayer = new DataLayer(headerDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewportLayer, (SelectionLayer) null);

		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

		ConfigRegistry configRegistry = new ConfigRegistry();

		Style style = new Style();
		style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 1);

		Style linkStyle = new Style();
		linkStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
				mainComposite.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		linkStyle.setAttributeValue(CellStyleAttributes.TEXT_DECORATION, TextDecorationEnum.UNDERLINE);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, linkStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 0);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, linkStyle, DisplayMode.SELECT,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 0);

		LinkClickConfiguration<Bug> linkClickConfiguration = new LinkClickConfiguration<>(
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 0);
		linkClickConfiguration.addClickListener((natTable, event) -> {
			NatEventData eventData = NatEventData.createInstanceFromEvent(event);
			int rowIndex = natTable.getRowIndexByPosition(eventData.getRowPosition());
			int columnIndex = natTable.getColumnIndexByPosition(eventData.getColumnPosition());

			Object cellData = dataProvider.getDataValue(columnIndex, rowIndex);

			if (cellData instanceof Integer) {
				try {
					URL url = new URL("https://bugs.eclipse.org/bugs/show_bug.cgi?id=" + String.valueOf(cellData));
					browserService.openExternalBrowser(url);
				} catch (MalformedURLException | CoreException e) {
					LOG.error(e.getMessage(), e);
					MessageDialog.openError(natTable.getShell(), "Error", e.getMessage());
				}
			}
		});

		natTable = new NatTable(mainComposite, compositeLayer, false);
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());
		natTable.addConfiguration(linkClickConfiguration);

		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

		natTable.configure();

		subscribeBugTable();
	}

	private void subscribeBugTable() {

		if (mostDiscussedBugQuery != null) {
			mostDiscussedBugQuery.dispose();
		}

		String queryProduct = preferences.get(Preferences.QUERY_PRODUCT, "Platform");
		List<String> queryProducts = null;
		if (!queryProduct.isEmpty()) {
			queryProducts = Arrays.asList(queryProduct.split(","));
		}
		String queryComponent = preferences.get(Preferences.QUERY_COMPONENT, "UI");
		List<String> queryComponents = null;
		if (!queryComponent.isEmpty()) {
			queryComponents = Arrays.asList(queryComponent.split(","));
		}
		Mono<List<Bug>> suitableBugs = prioritizerService.getMostDiscussedBugsOfTheMonth(queryProducts,
				queryComponents);

		eventList.clear();
		eventList.add(Bug.LOADING_DATA_FAKE_BUG);

		mostDiscussedBugQuery = suitableBugs.subscribeOn(Schedulers.elastic())
				.publishOn(SwtScheduler.from(mainComposite.getDisplay())).subscribe(bugsFromServer -> {
					eventList.clear();
					LOG.info("Anzahl gefundener Bugs: " + bugsFromServer.size());
					eventList.addAll(bugsFromServer);

					natTable.refresh(true);
				}, err -> {
					LOG.error(err.getMessage(), err);
					MessageDialog.openError(mainComposite.getShell(), "Error", err.getMessage());
				});
	}

	private void createSettings(Composite parent) {
		settingsComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(settingsComposite);

		Composite settingsPanel = new Composite(settingsComposite, SWT.NONE);

		Label productLabel = new Label(settingsPanel, SWT.FLAT);
		productLabel.setText("Product");

		String queryProduct = preferences.get(Preferences.QUERY_PRODUCT, "Platform");

		Text productText = new Text(settingsPanel, SWT.BORDER);
		productText.setText(queryProduct);
		productText.setToolTipText("Product");
		productText.setMessage("Product");
		productText.addModifyListener(event -> {
			preferences.put(Preferences.QUERY_PRODUCT, productText.getText());
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				LOG.error(e.getMessage(), e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		});

		Mono<List<String>> products = bugzillaService.getProducts();
		products.subscribeOn(Schedulers.elastic()).publishOn(SwtScheduler.from(parent.getDisplay())).subscribe(l -> {
			WidgetUtils.createContentAssist(productText, resourceManager, l.toArray(new String[l.size()]));
		});

		Label componentLabel = new Label(settingsPanel, SWT.FLAT);
		componentLabel.setText("Component");

		String queryComponent = preferences.get(Preferences.QUERY_COMPONENT, "UI");

		Text componentText = new Text(settingsPanel, SWT.BORDER);
		componentText.setText(queryComponent);
		componentText.setToolTipText("Component");
		componentText.setMessage("Component");
		componentText.addModifyListener(event -> {
			preferences.put(Preferences.QUERY_COMPONENT, componentText.getText());
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				LOG.error(e.getMessage(), e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		});
		
		Mono<List<String>> components = bugzillaService.getComponents();
		components.subscribeOn(Schedulers.elastic()).publishOn(SwtScheduler.from(parent.getDisplay())).subscribe(l -> {
			WidgetUtils.createContentAssist(componentText, resourceManager, l.toArray(new String[l.size()]));
		});

		GridLayoutFactory.swtDefaults().extendedMargins(5, 0, 0, 0).generateLayout(settingsPanel);
		GridDataFactory.fillDefaults().hint(300, SWT.DEFAULT).applyTo(settingsPanel);
	}

	@PreDestroy
	public void dispose() {
		mostDiscussedBugQuery.dispose();
	}

	@Inject
	@Optional
	public void toggleView(@UIEventTopic(Events.TOGGLE_VIEW_MOSTDISCUSSEDBUGSOFTHEMONTHPART) ViewType viewType) {
		// always switch to main, if another type is selected twice
		if ((currentViewType.equals(viewType)) || (ViewType.MAIN.equals(viewType))) {
			currentViewType = ViewType.MAIN;
			stackLayout.topControl = mainComposite;
			mainComposite.getParent().layout();
		} else if (ViewType.SETTINGS.equals(viewType)) {
			currentViewType = ViewType.SETTINGS;
			stackLayout.topControl = settingsComposite;
			settingsComposite.getParent().layout();
		}
	}

	@Inject
	@Optional
	public void refresh(@UIEventTopic(Events.REFRESH) boolean refresh) {
		subscribeBugTable();
	}

}