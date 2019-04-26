package com.vogella.prioritizer.ui.parts;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.PercentageDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.cell.AlternatingRowConfigLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultRowStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.hover.HoverLayer;
import org.eclipse.nebula.widgets.nattable.hover.config.SimpleHoverStylingBindings;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ButtonCellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
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
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.CellLabelMouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.nebula.widgets.proposal.ContentProposalAdapter;
import org.eclipse.nebula.widgets.proposal.DirectInputProposalConfigurator;
import org.eclipse.nebula.widgets.proposal.controladapter.TextControlAdapter;
import org.eclipse.nebula.widgets.suggestbox.ClosableSuggestBoxEntryImpl;
import org.eclipse.nebula.widgets.suggestbox.SuggestBox;
import org.eclipse.nebula.widgets.suggestbox.SuggestBoxEntry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.common.core.AgentIDGenerator;
import com.vogella.common.core.service.BrowserService;
import com.vogella.common.core.service.BugzillaService;
import com.vogella.prioritizer.core.events.Events;
import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.RankedBug;
import com.vogella.prioritizer.core.preferences.Preferences;
import com.vogella.prioritizer.core.service.PrioritizerService;
import com.vogella.prioritizer.ui.nattable.LinkClickConfiguration;
import com.vogella.prioritizer.ui.nattable.NatTableButtonTooltip;
import com.vogella.prioritizer.ui.nattable.RankedBugColumnPropertyAccessor;
import com.vogella.prioritizer.ui.nattable.RankedBugHeaderDataProvider;
import com.vogella.prioritizer.ui.nattable.SwitchImagePainter;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import reactor.core.Disposable;
import reactor.core.Disposables;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.swing.SwtScheduler;

@SuppressWarnings("restriction")
public class PrioritizerPart {

	private static final Logger LOG = LoggerFactory.getLogger(PrioritizerPart.class);

	@Inject
	@Preference
	private IEclipsePreferences preferences;

	@Inject
	private PrioritizerService prioritizerService;

	@Inject
	private BugzillaService bugzillaService;

	@Inject
	private BrowserService browserService;

	@Inject
	private IEventBroker eventBroker;

	@Inject
	private MPart part;

	private Disposable.Composite compositeDisposable = Disposables.composite();

	private StackLayout stackLayout;

	private Composite mainComposite;
	private ScrolledComposite scrolledComposite;

	private ViewType currentViewType = ViewType.MAIN;

	private EventList<RankedBug> eventList;

	private NatTable natTable;

	private RankedBugColumnPropertyAccessor bugColumnPropertyAccessor;

	private Browser browser;

	private ResourceManager resourceManager;

	public final static String ALL_DATA_SET = "ALL_DATA_SET";

	private String PRODUCT_DEFAULT = "Platform,JDT,PDE,EGit";
	private String COMPONENTS_DEFAULT = "UI,Core,Runtime,SWT,Text,Resources,Releng,Debug,IDE,Search";

	private Disposable likeSubscription;

	private Button applyAndSaveButton;

	private Text emailText;

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

		eventList = new BasicEventList<>(500);
		SortedList<RankedBug> sortedList = new SortedList<>(eventList, null);

		bugColumnPropertyAccessor = new RankedBugColumnPropertyAccessor();

		ListDataProvider<RankedBug> dataProvider = new ListDataProvider<>(sortedList, bugColumnPropertyAccessor);
		DataLayer dataLayer = new DataLayer(dataProvider);
		dataLayer.setColumnPercentageSizing(true);
		dataLayer.setColumnWidthPercentageByPosition(0, 7);
		dataLayer.setColumnWidthPercentageByPosition(1, 48);
		dataLayer.setColumnWidthPercentageByPosition(2, 11);
		dataLayer.setColumnWidthPercentageByPosition(3, 11);
		dataLayer.setColumnWidthPercentageByPosition(4, 11);
		dataLayer.setColumnWidthPercentageByPosition(5, 11);
		dataLayer.setColumnWidthPercentageByPosition(6, 4);
		dataLayer.setColumnWidthPercentageByPosition(7, 4);
		dataLayer.setColumnWidthPercentageByPosition(8, 4);
		HoverLayer hoverLayer = new HoverLayer(dataLayer, false);
		hoverLayer.addConfiguration(new SimpleHoverStylingBindings(hoverLayer));
		GlazedListsEventLayer<RankedBug> eventLayer = new GlazedListsEventLayer<RankedBug>(hoverLayer, sortedList);
		ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(eventLayer);
		ColumnLabelAccumulator columnLabelAccumulator = new ColumnLabelAccumulator(dataProvider);
		eventLayer.setConfigLabelAccumulator(columnLabelAccumulator);
		
//		SelectionLayer selectionLayer = new SelectionLayer(columnReorderLayer, false);
//		selectionLayer.setSelectionModel(
//				new RowSelectionModel<>(selectionLayer, dataProvider, new IRowIdAccessor<RankedBug>() {
//
//					@Override
//					public Serializable getRowId(RankedBug rowObject) {
//						return rowObject.getId();
//					}
//
//				}));
//
//		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

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
		
		compositeLayer.addConfiguration(new DefaultRowStyleConfiguration());
        compositeLayer.setConfigLabelAccumulatorForRegion(
                GridRegion.BODY,
                new AlternatingRowConfigLabelAccumulator(compositeLayer
                        .getChildLayerByRegionName(GridRegion.BODY)));

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

			RankedBug rowObject = dataProvider.getRowObject(rowIndex);
			if (cellData instanceof Number) {
				try {

					URL url = new URL("https://bugs.eclipse.org/bugs/show_bug.cgi?id=" + String.valueOf(cellData));
					browserService.openExternalBrowser(url);
				} catch (MalformedURLException | CoreException e) {
					LOG.error(e.getMessage(), e);
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

		Bundle bundle = FrameworkUtil.getBundle(getClass());

		URL delete = FileLocator.find(bundle, new Path("/icons/delete.png"));
		Image deleteImg = resourceManager.createImage(ImageDescriptor.createFromURL(delete));
		ButtonCellPainter notSuitableButton = createButtonToColumn(configRegistry,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 6, new ImagePainter(deleteImg));

		URL alarmSnooze = FileLocator.find(bundle, new Path("/icons/alarm-snooze.png"));
		Image alarmSnoozeImg = resourceManager.createImage(ImageDescriptor.createFromURL(alarmSnooze));
		ButtonCellPainter notNowButton = createButtonToColumn(configRegistry,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 7, new ImagePainter(alarmSnoozeImg));

		URL like = FileLocator.find(bundle, new Path("/icons/like.png"));
		URL dislike = FileLocator.find(bundle, new Path("/icons/unlike.png"));
		Image likeImg = resourceManager.createImage(ImageDescriptor.createFromURL(like));
		Image dislikeImg = resourceManager.createImage(ImageDescriptor.createFromURL(dislike));
		SwitchImagePainter switchImagePainter = new SwitchImagePainter(likeImg, dislikeImg, cell -> {
			int rowIndex = cell.getRowPosition();
			java.util.Optional<RankedBug> rankedBugByRow = getRankedBugByRow(rowIndex);
			if (rankedBugByRow.isPresent()) {
				RankedBug rankedBug = rankedBugByRow.get();
				return rankedBug.isLiked();
			}
			return false;
		});
		ButtonCellPainter likeButton = createButtonToColumn(configRegistry,
				ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 8, switchImagePainter);

		natTable = new NatTable(mainComposite, compositeLayer, false);
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());
		natTable.addConfiguration(linkClickConfiguration);
		natTable.addConfiguration(
				new ButtonClickConfiguration(notSuitableButton, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 6));
		natTable.addConfiguration(
				new ButtonClickConfiguration(notNowButton, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 7));
		natTable.addConfiguration(
				new ButtonClickConfiguration(likeButton, ColumnLabelAccumulator.COLUMN_LABEL_PREFIX + 8));
		
		// add the style configuration for hover
        natTable.addConfiguration(new AbstractRegistryConfiguration() {

            @Override
            public void configureRegistry(IConfigRegistry configRegistry) {
                // style that is applied when cells are hovered
                Style style = new Style();
                style.setAttributeValue(
                        CellStyleAttributes.BACKGROUND_COLOR,
                        GUIHelper.COLOR_GRAY);

                configRegistry.registerConfigAttribute(
                        CellConfigAttributes.CELL_STYLE,
                        style,
                        DisplayMode.HOVER);
            }
        });

		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

		natTable.configure();

		new NatTableButtonTooltip(natTable, x -> getRankedBugByRow(x).map(RankedBug::isLiked).orElse(Boolean.FALSE),
				GridRegion.BODY);

		subscribeBugTable();
	}

	private ButtonCellPainter createButtonToColumn(IConfigRegistry configRegistry, String configLabel,
			ICellPainter cellPainter) {
		ButtonCellPainter buttonPainter = new ButtonCellPainter(cellPainter);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, buttonPainter, DisplayMode.NORMAL,
				configLabel);

		// Add your listener to the button
		buttonPainter.addClickListener((natTable, event) -> {
			int row = this.natTable.getRowPositionByY(event.y);

			java.util.Optional<RankedBug> rankedBugByRow = getRankedBugByRow(row);

			// Get the bug if from the first row
			rankedBugByRow.ifPresent(rankedBug -> {

				String generatedAgentId = AgentIDGenerator.getAgentID();
				String agentId = preferences.get(Preferences.PRIORITIZER_AGENT_ID, generatedAgentId);
				String userEmail = preferences.get(Preferences.PRIORITIZER_USER_EMAIL, "");
				List<String> queryProduct = Arrays
						.asList(preferences.get(Preferences.PRIORITIZER_QUERY_PRODUCT, PRODUCT_DEFAULT).split(","));
				List<String> queryComponent = Arrays.asList(
						preferences.get(Preferences.PRIORITIZER_QUERY_COMPONENT, COMPONENTS_DEFAULT).split(","));

				int col = this.natTable.getColumnPositionByX(event.x);
				switch (col) {
				case 6:
					eventList.remove(rankedBug);
					prioritizerService.dislikeBug(agentId, rankedBug.getId(), userEmail, queryProduct, queryComponent)
							.subscribe(v -> {
							}, err -> {
								Bundle bundle = FrameworkUtil.getBundle(getClass());
								Status status = new Status(IStatus.ERROR, bundle.getSymbolicName(), err.getMessage(),
										err);
								ErrorDialog.openError(this.natTable.getShell(), "Error", err.getMessage(), status);
							});
					break;
				case 7:
					eventList.remove(rankedBug);
					int days = preferences.getInt(Preferences.PRIORITIZER_DEFER_DELAY, 30);
					prioritizerService
							.deferBug(agentId, rankedBug.getId(), days, userEmail, queryProduct, queryComponent)
							.subscribe(v -> {
							}, err -> {
								Bundle bundle = FrameworkUtil.getBundle(getClass());
								Status status = new Status(IStatus.ERROR, bundle.getSymbolicName(), err.getMessage(),
										err);
								ErrorDialog.openError(this.natTable.getShell(), "Error", err.getMessage(), status);
							});
					break;
				case 8:
					if (likeSubscription != null) {
						likeSubscription.dispose();
					}
					if (rankedBug.isLiked()) {
						likeSubscription = prioritizerService
								.unlikeBug(agentId, rankedBug.getId(), userEmail, queryProduct, queryComponent)
								.subscribe(rb -> {
									if (rb.isError()) {
										// reset like button on error
										rankedBug.setLiked(!rankedBug.isLiked());
										natTable.refresh(false);
									}
								}, err -> {
									Bundle bundle = FrameworkUtil.getBundle(getClass());
									Status status = new Status(IStatus.ERROR, bundle.getSymbolicName(),
											err.getMessage(), err);
									ErrorDialog.openError(this.natTable.getShell(), "Error", err.getMessage(), status);
								});
						rankedBug.setLiked(!rankedBug.isLiked());
						natTable.refresh(false);
					} else {
						likeSubscription = prioritizerService
								.likeBug(agentId, rankedBug.getId(), userEmail, queryProduct, queryComponent)
								.subscribe(rb -> {
									if (rb.isError()) {
										// reset like button on error
										rankedBug.setLiked(!rankedBug.isLiked());
										natTable.refresh(false);
									}
								}, err -> {
									Bundle bundle = FrameworkUtil.getBundle(getClass());
									Status status = new Status(IStatus.ERROR, bundle.getSymbolicName(),
											err.getMessage(), err);
									ErrorDialog.openError(this.natTable.getShell(), "Error", err.getMessage(), status);
								});
						rankedBug.setLiked(!rankedBug.isLiked());
						natTable.refresh(false);
					}
					break;
				default:
					break;
				}
			});
		});

		// Set the color of the cell. This is picked up by the button painter to
		// style the button
		Style style = new Style();
		style.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.COLOR_WHITE);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL, configLabel);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.SELECT, configLabel);

		return buttonPainter;
	}

	private java.util.Optional<RankedBug> getRankedBugByRow(int row) {
		Object dataValueByPosition = natTable.getDataValueByPosition(0, row);
		if (dataValueByPosition instanceof Number) {
			Number bugId = (Number) dataValueByPosition;

			return eventList.stream().filter(rb -> Objects.equals(bugId, rb.getId())).findAny();
		}
		return java.util.Optional.empty();
	}

	class ButtonClickConfiguration extends AbstractUiBindingConfiguration {

		private final ButtonCellPainter buttonCellPainter;
		private String configLabel;

		public ButtonClickConfiguration(ButtonCellPainter buttonCellPainter, String configLabel) {
			this.buttonCellPainter = buttonCellPainter;
			this.configLabel = configLabel;
		}

		/**
		 * Configure the UI bindings for the mouse click
		 */
		@Override
		public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
			// Match a mouse event on the body, when the left button is clicked
			// and the custom cell label is present
			CellLabelMouseEventMatcher mouseEventMatcher = new CellLabelMouseEventMatcher(GridRegion.BODY,
					MouseEventMatcher.LEFT_BUTTON, configLabel);

			// Inform the button painter of the click.
			uiBindingRegistry.registerMouseDownBinding(mouseEventMatcher, this.buttonCellPainter);
		}

	}

	private void subscribeBugTable() {
		String userEmail = preferences.get(Preferences.PRIORITIZER_USER_EMAIL, "");
		if (userEmail.isEmpty()) {
			return;
		}
		List<String> queryProduct = Arrays
				.asList(preferences.get(Preferences.PRIORITIZER_QUERY_PRODUCT, PRODUCT_DEFAULT).split(","));
		List<String> queryComponent = Arrays
				.asList(preferences.get(Preferences.PRIORITIZER_QUERY_COMPONENT, COMPONENTS_DEFAULT).split(","));

		String generatedAgentId = AgentIDGenerator.getAgentID();

		String agentId = preferences.get(Preferences.PRIORITIZER_AGENT_ID, generatedAgentId);
		Mono<List<RankedBug>> suitableBugs = prioritizerService.getSuitableBugs(agentId, userEmail, queryProduct,
				queryComponent);

		eventList.clear();
		eventList.add(RankedBug.LOADING_DATA_FAKE_BUG);

		compositeDisposable.add(suitableBugs.subscribeOn(Schedulers.elastic())
				.publishOn(SwtScheduler.from(mainComposite.getDisplay())).subscribe(bugsFromServer -> {
					eventList.clear();
					LOG.info("Anzahl gefundener Bugs: " + bugsFromServer.size());

					Collections.sort(bugsFromServer);
					eventList.addAll(bugsFromServer);

					natTable.refresh(true);
				}, err -> {
					LOG.error(err.getMessage(), err);
//					MessageDialog.openError(mainComposite.getShell(), "Error", err.getMessage());
				}));
	}

	private void createSettings(Composite parent) {

		scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite settingsComposite = new Composite(scrolledComposite, SWT.NONE);
		GridLayoutFactory.fillDefaults().numColumns(2).applyTo(settingsComposite);
		scrolledComposite.setContent(settingsComposite);

		Composite settingsPanel = new Composite(settingsComposite, SWT.NONE);

		Label emailLabel = new Label(settingsPanel, SWT.FLAT);
		emailLabel.setText("Enter your Email used in Bugzilla");

		String userEmail = preferences.get(Preferences.PRIORITIZER_USER_EMAIL, "");

		emailText = new Text(settingsPanel, SWT.BORDER);

		emailText.setToolTipText("Enter your Email used in Bugzilla");
		emailText.setMessage("Email");
		emailText.addModifyListener(event -> {
			if (emailText.getText().isEmpty()) {
				toogleRelevantUserInterfaceComponents(false);
				return;
			} else {
				toogleRelevantUserInterfaceComponents(true);
				preferences.put(Preferences.PRIORITIZER_USER_EMAIL, ((Text) event.getSource()).getText());
				try {
					preferences.flush();
				} catch (BackingStoreException e) {
					LOG.error(e.getMessage(), e);
					MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
				}
			}

		});

		Label productLabel = new Label(settingsPanel, SWT.FLAT);
		productLabel.setText("Product");

		SuggestBox<String> suggestBoxProduct = new SuggestBox<>(settingsPanel, SWT.NONE);
		suggestBoxProduct.getTextControl().setMessage("Product");
		suggestBoxProduct.getTextControl().setToolTipText("Product");
		String queryProduct = preferences.get(Preferences.PRIORITIZER_QUERY_PRODUCT, PRODUCT_DEFAULT);
		String[] savedProducts = queryProduct.split(",");
		for (String string : savedProducts) {
			suggestBoxProduct.addBox(new ClosableSuggestBoxEntryImpl<String>(string));
		}

		Flux<String> products = bugzillaService.getProducts().flatMapMany(Flux::fromIterable);
		TextControlAdapter productControlAdapter = new TextControlAdapter(suggestBoxProduct.getTextControl());
		DirectInputProposalConfigurator<String> productProposalConfigurator = new DirectInputProposalConfigurator<>(
				products);
		ContentProposalAdapter<String> productProposalAdapter = new ContentProposalAdapter<>(productControlAdapter,
				productProposalConfigurator);
		productProposalAdapter.addSelectionChangedListener(selection -> {
			IStructuredSelection sSel = (IStructuredSelection) selection;
			if (!selection.isEmpty()) {
				suggestBoxProduct.addBox(new ClosableSuggestBoxEntryImpl<String>(sSel.getFirstElement().toString()));
				suggestBoxProduct.getTextControl().setText("");
			}
		});

		Consumer<SuggestBoxEntry<String>> productChangeListener = t -> {
			Collection<String> elements = suggestBoxProduct.getElements();
			preferences.put(Preferences.PRIORITIZER_QUERY_PRODUCT, elements.stream().collect(Collectors.joining(",")));
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				LOG.error(e.getMessage(), e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		};

		suggestBoxProduct.addSuggestBoxEntryAddedListener(productChangeListener);
		suggestBoxProduct.addSuggestBoxEntryRemovedListener(productChangeListener);
		GridDataFactory.fillDefaults().hint(300, 60).grab(true, true).applyTo(suggestBoxProduct);

		Label componentLabel = new Label(settingsPanel, SWT.FLAT);
		componentLabel.setText("Component");

		SuggestBox<String> suggestBoxComponent = new SuggestBox<>(settingsPanel, SWT.NONE);
		suggestBoxComponent.getTextControl().setMessage("Component");
		suggestBoxComponent.getTextControl().setToolTipText("Component");
		String queryComponent = preferences.get(Preferences.PRIORITIZER_QUERY_COMPONENT, COMPONENTS_DEFAULT);
		String[] savedComponents = queryComponent.split(",");
		for (String string : savedComponents) {
			suggestBoxComponent.addBox(new ClosableSuggestBoxEntryImpl<String>(string));
		}

		Flux<String> components = bugzillaService.getComponents().flatMapMany(Flux::fromIterable);
		TextControlAdapter componentControlAdapter = new TextControlAdapter(suggestBoxComponent.getTextControl());
		DirectInputProposalConfigurator<String> componentProposalConfigurator = new DirectInputProposalConfigurator<>(
				components);
		ContentProposalAdapter<String> componentProposalAdapter = new ContentProposalAdapter<>(componentControlAdapter,
				componentProposalConfigurator);
		componentProposalAdapter.addSelectionChangedListener(selection -> {
			IStructuredSelection sSel = (IStructuredSelection) selection;
			if (!selection.isEmpty()) {
				suggestBoxComponent.addBox(new ClosableSuggestBoxEntryImpl<String>(sSel.getFirstElement().toString()));
				suggestBoxComponent.getTextControl().setText("");
			}
		});

		Consumer<SuggestBoxEntry<String>> componentChangeListener = t -> {
			Collection<String> elements = suggestBoxComponent.getElements();
			preferences.put(Preferences.PRIORITIZER_QUERY_COMPONENT,
					elements.stream().collect(Collectors.joining(",")));
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				LOG.error(e.getMessage(), e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		};

		suggestBoxComponent.addSuggestBoxEntryAddedListener(componentChangeListener);
		suggestBoxComponent.addSuggestBoxEntryRemovedListener(componentChangeListener);
		GridDataFactory.fillDefaults().hint(300, 60).grab(true, true).applyTo(suggestBoxComponent);

		new Label(settingsPanel, SWT.FLAT).setText("User defined keywords");

		String userKeywords = preferences.get(Preferences.PRIORITIZER_USER_KEYWORDS, "");
		Text keywordText = new Text(settingsPanel, SWT.BORDER);
		keywordText.setText(userKeywords);
		keywordText.setMessage("Your desired keywords for the bug search");
		keywordText.setToolTipText("Your desired keywords for the bug search");
		keywordText.addModifyListener(event -> {
			Text source = (Text) event.getSource();
			preferences.put(Preferences.PRIORITIZER_USER_KEYWORDS, source.getText());
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				LOG.error(e.getMessage(), e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		});

		new Label(settingsPanel, SWT.FLAT).setText("Snooze Bug for (days)");

		Text snoozeText = new Text(settingsPanel, SWT.BORDER);
		snoozeText.setText("30");
		snoozeText.setMessage("Days to snooze bug");
		snoozeText.setToolTipText("Days to snooze bug");
		snoozeText.addModifyListener(event -> {
			Text source = (Text) event.getSource();
			preferences.put(Preferences.PRIORITIZER_DEFER_DELAY, source.getText());
			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				LOG.error(e.getMessage(), e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		});

		Label agentLabel = new Label(settingsPanel, SWT.FLAT);
		agentLabel.setText("Unique identifier (alphanumeric and length 9)");

		String generatedAgentId = AgentIDGenerator.getAgentID();

		String agentId = preferences.get(Preferences.PRIORITIZER_AGENT_ID, generatedAgentId);

		Text agentText = new Text(settingsPanel, SWT.BORDER);
		agentText.setText(agentId);
		agentText.setToolTipText(
				"Unique identifier (must have the length of 9 and include only alphanumeric characters (a-z, A-Z, and 0-9). If the Plug-in is called from different computers for the same Email always use the same identfier");
		agentText.setMessage("Agent-Id");
		agentText.addModifyListener(event -> {
			String strPattern = "^[a-zA-Z0-9]*$";
			String input = ((Text) event.getSource()).getText();
			if (input.matches(strPattern) && input.length() == 9) {
				preferences.put(Preferences.PRIORITIZER_AGENT_ID, input);
				agentText.setForeground(null);
				toogleRelevantUserInterfaceComponents(!emailText.getText().isEmpty());

			} else {
				agentText.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));
				toogleRelevantUserInterfaceComponents(false);
			}

			try {
				preferences.flush();
			} catch (BackingStoreException e) {
				LOG.error(e.getMessage(), e);
				MessageDialog.openError(settingsPanel.getShell(), "Error", e.getMessage());
			}
		});

		new Label(settingsPanel, SWT.NONE);
		applyAndSaveButton = new Button(settingsPanel, SWT.PUSH);
		applyAndSaveButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		applyAndSaveButton.setText("Apply and Save");
		applyAndSaveButton.addSelectionListener(SelectionListener.widgetSelectedAdapter(e ->

		{
			currentViewType = ViewType.MAIN;
			stackLayout.topControl = mainComposite;
			mainComposite.getParent().layout();
			refresh(true);
		}));

		GridLayoutFactory.swtDefaults().extendedMargins(5, 0, 0, 0).generateLayout(settingsPanel);
		GridDataFactory.fillDefaults().grab(true, true).hint(300, SWT.DEFAULT).applyTo(settingsPanel);

		browser = new Browser(settingsComposite, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(browser);

		subscribeChart();
		if (userEmail.isEmpty()) {
			toggleView(ViewType.SETTINGS);
		}
		scrolledComposite.setMinSize(settingsComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Composite p = scrolledComposite.getParent();
		while (p.getParent() != null) {
			p = p.getParent();
		}
		p.layout();
		// Now set the user value to trigger flatschi
		Display.getCurrent().asyncExec(() -> {
			emailText.setText(userEmail);
		});
	}

	private void toogleRelevantUserInterfaceComponents(boolean enabled) {
		part.getContext().set(ALL_DATA_SET, enabled);
		eventBroker.post(UIEvents.REQUEST_ENABLEMENT_UPDATE_TOPIC, UIEvents.ALL_ELEMENT_ID);
		applyAndSaveButton.setEnabled(enabled);
	}

	private void subscribeChart() {
		String userEmail = preferences.get(Preferences.PRIORITIZER_USER_EMAIL, "");
		if (userEmail.isEmpty()) {
			return;
		}
		List<String> queryProduct = Arrays
				.asList(preferences.get(Preferences.PRIORITIZER_QUERY_PRODUCT, PRODUCT_DEFAULT).split(","));
		List<String> queryComponent = Arrays
				.asList(preferences.get(Preferences.PRIORITIZER_QUERY_COMPONENT, COMPONENTS_DEFAULT).split(","));
		String generatedAgentId = AgentIDGenerator.getAgentID();

		String agentId = preferences.get(Preferences.PRIORITIZER_AGENT_ID, generatedAgentId);

		Mono<String> keywordImage = prioritizerService.getKeyWordUrl(agentId, userEmail, queryProduct, queryComponent);

		compositeDisposable.add(keywordImage.subscribeOn(Schedulers.elastic())
				.publishOn(SwtScheduler.from(scrolledComposite.getDisplay()))
				.doOnSubscribe(s -> browser.setText("<html><body><h3>Loading...</h3></body></html>"))
				.subscribe(url -> browser.setUrl(url), err -> LOG.error(err.getMessage(), err)));
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
			refresh(true);
			stackLayout.topControl = mainComposite;
			mainComposite.getParent().layout();
		} else if (ViewType.SETTINGS.equals(viewType)) {
			currentViewType = ViewType.SETTINGS;
			stackLayout.topControl = scrolledComposite;
			scrolledComposite.getParent().layout();
		}
	}

	@Inject
	@Optional
	public void refresh(@UIEventTopic(Events.REFRESH_PRIORITIZER) boolean refresh) {
		subscribeBugTable();
		subscribeChart();
	}

	@Focus
	public void focus() {
		stackLayout.topControl.setFocus();
	}

}
