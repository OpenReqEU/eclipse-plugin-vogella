
package com.vogella.tracing.core.addon;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.CommandManager;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.osgi.service.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.tracing.core.constants.CommandListenerEvents;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@SuppressWarnings("restriction")
public class CommandListenerAddon {

	private static final Logger LOG = LoggerFactory.getLogger(CommandListenerAddon.class);

	@Inject
	private MeterRegistry meterRegistry;

	private IExecutionListener executionListener;

	@Inject
	@Optional
	public void applicationStarted(@EventTopic(UIEvents.UILifeCycle.APP_STARTUP_COMPLETE) Event event,
			CommandManager commandManager, IEventBroker broker) {

		executionListener = new IExecutionListener() {

			@Override
			public void notHandled(String commandId, NotHandledException exception) {
				// TODO create constants
				Counter counter = meterRegistry.counter("command.calls", "commandId", commandId, "commandName",
						getCommandName(commandManager, commandId), "result", "notHandled");
				counter.increment();

				// always fire event after metrics are done
				broker.post(CommandListenerEvents.TOPIC_COMMAND_NOT_HANDLED, commandId);
			}

			@Override
			public void postExecuteFailure(String commandId, ExecutionException exception) {
				// TODO create constants
				Counter counter = meterRegistry.counter("command.calls", "commandId", commandId, "commandName",
						getCommandName(commandManager, commandId), "result", "failure");
				counter.increment();

				// always fire event after metrics are done
				broker.post(CommandListenerEvents.TOPIC_COMMAND_EXECUTE_FAILURE, commandId);
			}

			@Override
			public void postExecuteSuccess(String commandId, Object returnValue) {
				// TODO create constants
				Counter counter = meterRegistry.counter("command.calls", "commandId", commandId, "commandName",
						getCommandName(commandManager, commandId), "result", "success");
				counter.increment();

				// always fire event after metrics are done
				broker.post(CommandListenerEvents.TOPIC_COMMAND_POST_EXECUTE_SUCCESS, commandId);
			}

			@Override
			public void preExecute(String commandId, ExecutionEvent event) {
				// TODO create constants
				Counter counter = meterRegistry.counter("command.calls", "commandId", commandId, "commandName",
						getCommandName(commandManager, commandId), "result", "pre");
				counter.increment();

				// always fire event after metrics are done
				broker.post(CommandListenerEvents.TOPIC_COMMAND_PRE_EXECUTE, commandId);
			}

		};

		commandManager.addExecutionListener(executionListener);
	}

	@PreDestroy
	public void dispose(CommandManager commandManager) {
		commandManager.removeExecutionListener(executionListener);
	}

	private String getCommandName(CommandManager commandManager, String commandId) {
		Command command = commandManager.getCommand(commandId);
		try {
			return command.getName();
		} catch (NotDefinedException e) {
			// should not happen in this listener
			LOG.error(e.getMessage(), e);
		}
		return "No command name";
	}

}
