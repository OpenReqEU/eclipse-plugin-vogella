package com.vogella.tracing.core.listener;

import java.util.Optional;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.ui.bindings.EBindingService;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.commands.MCommand;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledItem;
import org.eclipse.e4.ui.workbench.renderers.swt.HandledContributionItem;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.SubContributionItem;
import org.eclipse.jface.bindings.TriggerSequence;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.menus.CommandContributionItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.common.core.domain.CommandStats;
import com.vogella.common.core.service.CommandStatsPersistenceService;

@SuppressWarnings("restriction")
public class SelectionDisplayFilter implements Listener {

	private static final Logger LOG = LoggerFactory.getLogger(SelectionDisplayFilter.class);
	private CommandStatsPersistenceService commandStatsPersistenceService;
	private ECommandService commandService;
	private EBindingService bindingService;
	private MApplication app;

	private enum CommandCallOrigin {
		MENU, TOOLBAR
	}

	private enum ContributionItemType {
		CommandContributionItem, HandledContributionItem, ActionContributionItem
	}

	private static class CommandData {
		final String commandId;
		final String commandName;
		final ContributionItemType contributionItemType;

		public CommandData(String commandId, String commandName, ContributionItemType contributionItemType) {
			this.commandId = commandId;
			this.commandName = commandName;
			this.contributionItemType = contributionItemType;
		}
	}

	public SelectionDisplayFilter(CommandStatsPersistenceService commandStatsPersistenceService,
			ECommandService commandService, EBindingService bindingService, MApplication app) {
		this.commandStatsPersistenceService = commandStatsPersistenceService;
		this.commandService = commandService;
		this.bindingService = bindingService;
		this.app = app;
	}

	@Override
	public void handleEvent(Event event) {
		Widget widget = event.widget;

		if (widget instanceof MenuItem) {
			MenuItem menuItem = (MenuItem) widget;

			int menuDepth = getMenuDepth(menuItem.getParent(), 0);

			LOG.debug(menuItem.getText() + " has a depth of " + menuDepth);

			handleItemData(menuItem.getData(), CommandCallOrigin.MENU, menuDepth);
		} else if (widget instanceof ToolItem) {
			handleItemData(widget.getData(), CommandCallOrigin.TOOLBAR, 0);
		}
	}

	private void handleItemData(Object data, CommandCallOrigin origin, int menuDepth) {
		Optional<CommandData> commandData = getCommandData(data);

		commandData.ifPresent(cmdData -> {
			Optional<CommandStats> optional = commandStatsPersistenceService.get(cmdData.commandId);

			if (optional.isPresent()) {
				optional.get().incrementInvocations();
			} else {
				CommandStats commandStats = new CommandStats();
				commandStats.setCommandId(cmdData.commandId);
				commandStats.setCommandName(cmdData.commandName);

				setKeyBinding(cmdData, commandStats);

				commandStats.setMenuDepth(menuDepth);
				commandStats.setInvocations(1);

				commandStatsPersistenceService.save(commandStats);
			}
		});
	}

	private void setKeyBinding(CommandData cmdData, CommandStats commandStats) {
		ParameterizedCommand command = commandService.createCommand(cmdData.commandId, null);
		if (command != null) {
			TriggerSequence bestSequenceFor = bindingService.getBestSequenceFor(command);
			if (bestSequenceFor != null) {
				commandStats.setKeybinding(bestSequenceFor.format());
			}
		}
	}

	private Optional<CommandData> getCommandData(Object data) {
		if (data instanceof CommandContributionItem) {
			ParameterizedCommand command = ((CommandContributionItem) data).getCommand();
			MCommand mCommand = app.getCommand(command.getId());
			CommandData commandData = new CommandData(command.getId(), mCommand.getLocalizedCommandName(),
					ContributionItemType.CommandContributionItem);
			return Optional.of(commandData);
		} else if (data instanceof HandledContributionItem) {
			MHandledItem model = ((HandledContributionItem) data).getModel();
			MCommand command = model.getCommand();
			CommandData commandData = new CommandData(command.getElementId(), command.getLocalizedCommandName(),
					ContributionItemType.HandledContributionItem);
			return Optional.of(commandData);
		} else if (data instanceof ActionContributionItem) {
			IAction action = ((ActionContributionItem) data).getAction();
			CommandData commandData = new CommandData(action.getActionDefinitionId(), action.getText(),
					ContributionItemType.ActionContributionItem);
			return Optional.of(commandData);
		} else if (data instanceof SubContributionItem) {
			return getCommandData(((SubContributionItem) data).getInnerItem());
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
