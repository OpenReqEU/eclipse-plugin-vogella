
package com.vogella.tracing.ui.parts;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.bindings.TriggerSequence;
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

import com.vogella.tracing.core.constants.CommandListenerEvents;
import com.vogella.tracing.ui.domain.CommandStats;
import com.vogella.tracing.ui.nattable.CommandStatsColumnPropertyAccessor;
import com.vogella.tracing.ui.nattable.CommandStatsHeaderDataProvider;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

@SuppressWarnings("restriction")
public class CommandStatsPart {

	public static final String COMMAND_STATS_SELECTION = "CommandStatsSelection";

	@Inject
	private MeterRegistry meterRegistry;

	@Inject
	private EBindingService bindingService;

	@Inject
	private ECommandService commandService;

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
		List<Meter> meters = meterRegistry.getMeters();
		if (meterRegistry instanceof CompositeMeterRegistry) {
			Set<MeterRegistry> registries = ((CompositeMeterRegistry) meterRegistry).getRegistries();
			java.util.Optional<MeterRegistry> findAny = registries.stream()
					.filter(SimpleMeterRegistry.class::isInstance).findAny();
			if (findAny.isPresent()) {
				meters = findAny.get().getMeters();
			}
		}
		List<CommandStats> list = meters.stream()
				.filter(meter -> "command.calls.contributionitem".equals(meter.getId().getName())).flatMap(meter -> {
					return StreamSupport.stream(meter.measure().spliterator(), false).map(measurement -> {
						String cmdId = meter.getId().getTag("commandId");
						double invocations = measurement.getValue();

						ParameterizedCommand command = commandService.createCommand(cmdId, null);
						return new CommandStats(cmdId, getCommandName(command), (int) invocations, getKeybinding(command));
					});
				}).collect(Collectors.toList());

		sortedList.clear();
		sortedList.addAll(list);
		natTable.refresh();
	}

	private String getKeybinding(ParameterizedCommand command) {
		TriggerSequence bestSequenceFor = bindingService.getBestSequenceFor(command);
		if (bestSequenceFor != null) {
			return bestSequenceFor.format();
		}
		return "No keybinding definied";
	}

	private String getCommandName(ParameterizedCommand command) {
		try {
			return command.getName();
		} catch (NotDefinedException e) {
			// unlikely to happen
			return "Command does not have a name";
		}
	}
}
