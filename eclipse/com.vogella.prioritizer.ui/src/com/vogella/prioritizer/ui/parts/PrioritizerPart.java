package com.vogella.prioritizer.ui.parts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.PercentageDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.PercentageBarCellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.PercentageBarDecorator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.style.TextDecorationEnum;
import org.eclipse.nebula.widgets.nattable.style.VerticalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

import com.vogella.prioritizer.core.events.Events;
import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.RankedBug;
import com.vogella.prioritizer.core.preferences.Preferences;
import com.vogella.prioritizer.core.service.BrowserService;
import com.vogella.prioritizer.core.service.PrioritizerService;
import com.vogella.prioritizer.ui.nattable.LinkClickConfiguration;
import com.vogella.prioritizer.ui.nattable.RankedBugColumnPropertyAccessor;
import com.vogella.prioritizer.ui.nattable.RankedBugHeaderDataProvider;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.swing.SwtScheduler;

@SuppressWarnings("restriction")
public class PrioritizerPart {

	@Inject
	@Preference
	private IEclipsePreferences preferences;

	@Inject
	private PrioritizerService prioritizerService;

	@Inject
	private Logger log;

	@Inject
	private BrowserService browserService;

	private Disposable.Composite compositeDisposable = Disposables.composite();

	private StackLayout stackLayout;

	private Composite settingsComposite;

	private Composite mainComposite;

	private ViewType currentViewType = ViewType.MAIN;

	private EventList<RankedBug> eventList;

	private NatTable natTable;

	private RankedBugColumnPropertyAccessor bugColumnPropertyAccessor;

	private Browser browser;

	@PostConstruct
	public void createPartControl(Composite parent) {
		stackLayout = new StackLayout();
		parent.setLayout(stackLayout);

		createNatTable(parent);

		createSettings(parent);
	}

	private void createNatTable(Composite parent) {

		mainComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().applyTo(mainComposite);

		stackLayout.topControl = mainComposite;

		eventList = new BasicEventList<>(500);
		SortedList<RankedBug> sortedList = new SortedList<>(eventList, null);

		bugColumnPropertyAccessor = new RankedBugColumnPropertyAccessor();

		ListDataProvider<RankedBug> dataProvider = new ListDataProvider<>(sortedList, bugColumnPropertyAccessor);
		DataLayer dataLayer = new DataLayer(dataProvider);
		dataLayer.setColumnPercentageSizing(true);
		dataLayer.setColumnWidthPercentageByPosition(0, 7);
		dataLayer.setColumnWidthPercentageByPosition(1, 60);
		dataLayer.setColumnWidthPercentageByPosition(2, 11);
		dataLayer.setColumnWidthPercentageByPosition(3, 11);
		dataLayer.setColumnWidthPercentageByPosition(4, 11);
		GlazedListsEventLayer<RankedBug> eventLayer = new GlazedListsEventLayer<RankedBug>(dataLayer, sortedList);
		ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(eventLayer);
		ColumnLabelAccumulator columnLabelAccumulator = new ColumnLabelAccumulator(dataProvider);
		eventLayer.setConfigLabelAccumulator(columnLabelAccumulator);

		ViewportLayer viewportLayer = new ViewportLayer(columnReorderLayer);

		IDataProvider headerDataProvider = new RankedBugHeaderDataProvider();
		DataLayer headerDataLayer = new DataLayer(headerDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewportLayer, (SelectionLayer) null);
		ConfigRegistry configRegistry = new ConfigRegistry();
		final SortHeaderLayer<RankedBug> sortHeaderLayer = new SortHeaderLayer<>(columnHeaderLayer,
				new GlazedListsSortModel<>(sortedList, bugColumnPropertyAccessor, configRegistry, headerDataLayer));

		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, sortHeaderLayer, 0, 0);
		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

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
					log.error(e);
					MessageDialog.openError(natTable.getShell(), "Error", e.getMessage());
				}
			}
		});

		Style cellStyle = new Style();
		cellStyle.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.CENTER);
		cellStyle.setAttributeValue(CellStyleAttributes.VERTICAL_ALIGNMENT, VerticalAlignmentEnum.MIDDLE);

		cellStyle.setAttributeValue(PercentageBarDecorator.PERCENTAGE_BAR_COMPLETE_REGION_START_COLOR,
				GUIHelper.getColor(new RGB(55, 186, 68)));
		cellStyle.setAttributeValue(PercentageBarDecorator.PERCENTAGE_BAR_COMPLETE_REGION_END_COLOR,
				GUIHelper.getColor(new RGB(94, 253, 0)));
		cellStyle.setAttributeValue(PercentageBarDecorator.PERCENTAGE_BAR_INCOMPLETE_REGION_COLOR,
				GUIHelper.getColor(new RGB(244, 244, 244)));

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, cellStyle, DisplayMode.NORMAL,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 2);

		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, new PercentageDisplayConverter(),
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 2);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, new PercentageBarCellPainter(),
				DisplayMode.NORMAL, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 2);

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
		String userEmail = preferences.get(Preferences.USER_EMAIL, "simon.scholz@vogella.com");
		List<String> queryProduct = Arrays.asList(preferences.get(Preferences.QUERY_PRODUCT, "Platform").split(","));
		List<String> queryComponent = Arrays.asList(preferences.get(Preferences.QUERY_COMPONENT, "UI").split(","));
		Mono<List<RankedBug>> suitableBugs = prioritizerService.getSuitableBugs(userEmail, queryProduct,
				queryComponent);

		eventList.clear();
		eventList.add(RankedBug.LOADING_DATA_FAKE_BUG);

		compositeDisposable.add(suitableBugs.subscribeOn(Schedulers.elastic())
				.publishOn(SwtScheduler.from(mainComposite.getDisplay())).subscribe(bugsFromServer -> {
					eventList.clear();
					System.out.println("Anzahl gefundener Bugs: " + bugsFromServer.size());
					eventList.addAll(bugsFromServer);

					OptionalDouble min = eventList.stream().mapToDouble(RankedBug::getPriority).min();
					OptionalDouble max = eventList.stream().mapToDouble(RankedBug::getPriority).max();

					if (min.isPresent() && max.isPresent()) {
						bugColumnPropertyAccessor.setMinAndMax(min.getAsDouble(), max.getAsDouble());
					}
					natTable.refresh(true);
				}, err -> {
					log.error(err);
					MessageDialog.openError(mainComposite.getShell(), "Error", err.getMessage());
				}));
	}

	private void createSettings(Composite parent) {
		settingsComposite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(settingsComposite);

		Composite settingsPanel = new Composite(settingsComposite, SWT.NONE);

		Label emailLabel = new Label(settingsPanel, SWT.FLAT);
		emailLabel.setText("Email");

		String userEmail = preferences.get(Preferences.USER_EMAIL, "simon.scholz@vogella.com");

		Text emailText = new Text(settingsPanel, SWT.BORDER);
		emailText.setText(userEmail);
		emailText.setToolTipText("Email");
		emailText.setMessage("Email");
		emailText.addModifyListener(event -> {
			preferences.put(Preferences.USER_EMAIL, emailText.getText());
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				log.error(e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		});

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
				log.error(e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
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
				log.error(e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		});

		GridLayoutFactory.fillDefaults().generateLayout(settingsPanel);
		GridDataFactory.fillDefaults().hint(300, SWT.DEFAULT).applyTo(settingsPanel);

		browser = new Browser(settingsComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);

		subscribeChart();
	}

	private void subscribeChart() {
		String userEmail = preferences.get(Preferences.USER_EMAIL, "simon.scholz@vogella.com");
		List<String> queryProduct = Arrays.asList(preferences.get(Preferences.QUERY_PRODUCT, "Platform").split(","));
		List<String> queryComponent = Arrays.asList(preferences.get(Preferences.QUERY_COMPONENT, "UI").split(","));
		Mono<String> keywordImage = prioritizerService.getKeyWordUrl(userEmail, queryProduct, queryComponent);

		compositeDisposable.add(keywordImage.subscribeOn(Schedulers.elastic())
				.publishOn(SwtScheduler.from(settingsComposite.getDisplay())).subscribe(url -> {
					browser.setUrl(url);
				}, err -> {
					MessageDialog.openError(settingsComposite.getShell(), "Error", err.getMessage());
				}));
	}

	@PreDestroy
	public void dispose() {
		compositeDisposable.dispose();
	}

	@Inject
	@Optional
	public void toggleView(@UIEventTopic(Events.TOGGLE_VIEW_PRIORITIZERPART) ViewType viewType) {
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
		subscribeChart();
	}
}
