package com.vogella.tracing.core.listener;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.Optional;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.renderers.swt.HandledContributionItem;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.menus.CommandContributionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;

@SuppressWarnings("restriction")
public class SelectionDisplayFilter implements Listener {

	private static final Logger LOG = LoggerFactory.getLogger(SelectionDisplayFilter.class);

	private enum CommandCallOrigin {
		MENU, TOOLBAR
	}

	private MeterRegistry meterRegistry;

	private Counter menuSelectionCounter;
	private Counter toolbarSelectionCounter;

	public SelectionDisplayFilter(MeterRegistry meterRegistry) {
		this.meterRegistry = meterRegistry;
		menuSelectionCounter = Counter.builder("selection.menu").tags("selection", "menu").register(meterRegistry);
		toolbarSelectionCounter = Counter.builder("selection.toolbar").tags("selection", "toolbar")
				.register(meterRegistry);
	}

	@Override
	public void handleEvent(Event event) {
		Widget widget = event.widget;

		if (widget instanceof MenuItem) {
			MenuItem menuItem = (MenuItem) widget;
			menuSelectionCounter.increment();

			int menuDepth = getMenuDepth(menuItem.getParent(), 0);
			DistributionSummary menuDepthCounter = meterRegistry.summary("selection.menu", "menuText",
					menuItem.getText());
			menuDepthCounter.record(menuDepth);

			LOG.debug(menuItem.getText() + " has a depth of " + menuDepth);

			handleItemData(menuItem.getData(), CommandCallOrigin.MENU);
		} else if (widget instanceof ToolItem) {
			toolbarSelectionCounter.increment();
			handleItemData(widget.getData(), CommandCallOrigin.TOOLBAR);
		}
	}

	private void handleItemData(Object data, CommandCallOrigin origin) {
		Optional<Entry<String, String>> commandIdAndName = getCommandIdAndName(data);

		commandIdAndName.ifPresent(idAndName -> {
			Counter counter = meterRegistry.counter("command.calls.contributionitem", "commandId", idAndName.getKey(),
					"commandName", idAndName.getValue(), "origin", origin.toString());
			counter.increment();
		});
	}

	private Optional<Entry<String, String>> getCommandIdAndName(Object data) {
		if (data instanceof CommandContributionItem) {
			ParameterizedCommand command = ((CommandContributionItem) data).getCommand();
			try {
				String id = command.getId();
				String name = command.getName();
				return Optional.of(new AbstractMap.SimpleEntry<>(id, name));
			} catch (NotDefinedException e) {
				LOG.error(e.getMessage(), e);
			}
		} else if (data instanceof HandledContributionItem) {
			MHandledItem model = ((HandledContributionItem) data).getModel();
			MCommand command = model.getCommand();
			String elementId = command.getElementId();
			String commandName = command.getCommandName();
			return Optional.of(new AbstractMap.SimpleEntry<>(elementId, commandName));
		} else if (data instanceof ActionContributionItem) {
			IAction action = ((ActionContributionItem) data).getAction();
			String id = action.getId();
			String actionText = action.getText();
			return Optional.of(new AbstractMap.SimpleEntry<>(id, actionText));
		} else if (data instanceof SubContributionItem) {
			return getCommandIdAndName(((SubContributionItem) data).getInnerItem());
		}

		return Optional.empty();
	}

	private int getMenuDepth(Menu parentMenu, int i) {
		if (parentMenu.getParentMenu() != null) {
			return getMenuDepth(parentMenu.getParentMenu(), ++i);
		}

		return i;
	}
}
