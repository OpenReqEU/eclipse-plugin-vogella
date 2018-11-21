
package com.vogella.tracing.ui.parts;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.RowSelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionUtils;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultRowSelectionLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.event.ISelectionEvent;
import org.eclipse.nebula.widgets.nattable.sort.config.SingleClickSortConfiguration;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;

import com.vogella.common.core.domain.CommandStats;
import com.vogella.common.core.service.CommandStatsPersistenceService;
import com.vogella.tracing.core.constants.CommandListenerEvents;
import com.vogella.tracing.ui.nattable.CommandStatsColumnPropertyAccessor;
import com.vogella.tracing.ui.nattable.CommandStatsHeaderDataProvider;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;

public class CommandStatsPart {

	public static final String COMMAND_STATS_SELECTION = "CommandStatsSelection";

	@Inject
	private CommandStatsPersistenceService commandStatsPersistenceService;

	private NatTable natTable;

	private SortedList<CommandStats> sortedList;

	@PostConstruct
	public void createPartControl(Composite parent, MPart part) {
		BasicEventList<CommandStats> eventList = new BasicEventList<>(500);
		sortedList = new SortedList<>(eventList, (o1, o2) -> Double.compare(o2.getInvocations(), o1.getInvocations()));

		ListDataProvider<CommandStats> dataProvider = new ListDataProvider<CommandStats>(sortedList,
				new CommandStatsColumnPropertyAccessor());
		DataLayer dataLayer = new DataLayer(dataProvider);
		dataLayer.setColumnPercentageSizing(true);
		dataLayer.setColumnWidthPercentageByPosition(0, 40);
		dataLayer.setColumnWidthPercentageByPosition(1, 30);
		dataLayer.setColumnWidthPercentageByPosition(2, 10);
		dataLayer.setColumnWidthPercentageByPosition(3, 20);
		ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(dataLayer);
		ColumnLabelAccumulator columnLabelAccumulator = new ColumnLabelAccumulator(dataProvider);
		dataLayer.setConfigLabelAccumulator(columnLabelAccumulator);

		SelectionLayer selectionLayer = new SelectionLayer(columnReorderLayer, false);
		RowSelectionModel<CommandStats> rowSelectionModel = new RowSelectionModel<>(selectionLayer, dataProvider,
				rowObject -> rowObject.getCommandId());
		rowSelectionModel.setMultipleSelectionAllowed(false);
		selectionLayer.setSelectionModel(rowSelectionModel);

		selectionLayer.addConfiguration(new DefaultRowSelectionLayerConfiguration());

		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		IDataProvider headerDataProvider = new CommandStatsHeaderDataProvider();
		DataLayer headerDataLayer = new DataLayer(headerDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewportLayer, (SelectionLayer) null);

		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

		ConfigRegistry configRegistry = new ConfigRegistry();

		natTable = new NatTable(parent, compositeLayer, false);
		natTable.setConfigRegistry(configRegistry);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new SingleClickSortConfiguration());

		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

		natTable.configure();

		selectionLayer.addLayerListener(event -> {
			if (event instanceof ISelectionEvent) {
				List<CommandStats> selection = SelectionUtils.getSelectedRowObjects(selectionLayer, dataProvider, true);
				part.getContext().set(COMMAND_STATS_SELECTION, selection);
			}
		});

		refreshCommandStats("");
	}

	@Inject
	@Optional
	public void refreshCommandStats(@UIEventTopic(CommandListenerEvents.TOPIC_COMMAND_PRE_EXECUTE) String commandId) {
		Collection<CommandStats> commandStats = commandStatsPersistenceService.get();
		sortedList.clear();
		sortedList.addAll(commandStats);
		natTable.refresh();
	}
}
