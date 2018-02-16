package com.vogella.prioritizer.ui.parts;

import java.io.ByteArrayInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultFloatDisplayConverter;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.BackingStoreException;

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
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.swt.schedulers.SwtSchedulers;

@SuppressWarnings("restriction")
public class PrioritizerView {

	public static enum ViewType {
		MAIN, SETTINGS
	}

	@Inject
	@Preference
	private IEclipsePreferences preferences;

	@Inject
	private PrioritizerService prioritizerService;

	@Inject
	private Logger log;

	@Inject
	private BrowserService browserService;

	private CompositeDisposable compositeDisposable = new CompositeDisposable();

	private StackLayout stackLayout;

	private ResourceManager resourceManager;

	private Composite settingsComposite;

	private Composite mainComposite;

	private ViewType currentViewType = ViewType.MAIN;

	private Label imgLabel;

	private EventList<Bug> eventList;

	private NatTable natTable;

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

		eventList = new BasicEventList<>(50);

		ListDataProvider<Bug> dataProvider = new ListDataProvider<>(eventList, new BugColumnPropertyAccessor());
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

		ViewportLayer viewportLayer = new ViewportLayer(columnReorderLayer);

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

		DefaultFloatDisplayConverter floatDisplayConverter = new DefaultFloatDisplayConverter(true);
		NumberFormat formatter = new DecimalFormat("0.00");
		floatDisplayConverter.setNumberFormat(formatter);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, floatDisplayConverter,
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
		String queryProduct = preferences.get(Preferences.QUERY_PRODUCT, "Platform");
		String queryComponent = preferences.get(Preferences.QUERY_COMPONENT, "UI");
		Single<List<Bug>> suitableBugs = prioritizerService.getSuitableBugs(userEmail, queryProduct, queryComponent,
				500);

		eventList.clear();
		eventList.add(Bug.LOADING_DATA_FAKE_BUG);

		compositeDisposable.add(suitableBugs.subscribeOn(Schedulers.io())
				.observeOn(SwtSchedulers.from(mainComposite.getDisplay())).subscribe(bugsFromServer -> {
					eventList.clear();
					eventList.addAll(bugsFromServer);
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

		imgLabel = new Label(settingsComposite, SWT.FLAT);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(imgLabel);

		subscribeChart();
	}

	private void subscribeChart() {
		String userEmail = preferences.get(Preferences.USER_EMAIL, "simon.scholz@vogella.com");
		Single<byte[]> keywordImage = prioritizerService.getKeyWordImage(userEmail, null, null, 200);

		compositeDisposable.add(keywordImage.subscribeOn(Schedulers.io())
				.observeOn(SwtSchedulers.from(settingsComposite.getDisplay())).subscribe(imageBytes -> {

					ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);

					ImageData imageData = new ImageData(byteArrayInputStream);

					ImageDescriptor imageDescriptor = ImageDescriptor.createFromImageDataProvider(zoom -> {
						return imageData;
					});
					Image image = resourceManager.createImage(imageDescriptor);

					imgLabel.setImage(image);
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
	public void toggleView(@UIEventTopic(Events.TOGGLE_VIEW) ViewType viewType) {
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
