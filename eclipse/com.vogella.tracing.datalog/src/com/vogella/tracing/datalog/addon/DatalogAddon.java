
package com.vogella.tracing.datalog.addon;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vogella.tracing.core.constants.CommandListenerEvents;

@SuppressWarnings("restriction")
public class DatalogAddon {

	private static final Logger LOG = LoggerFactory.getLogger(DatalogAddon.class);

	@Inject
	@Optional
	public void commandNotHandled(@EventTopic(CommandListenerEvents.TOPIC_COMMAND_NOT_HANDLED) String commandId) {
		LOG.info("Command has not been handled (" + commandId + ")");
	}

	@Inject
	@Optional
	public void commandExecuteFailure(
			@EventTopic(CommandListenerEvents.TOPIC_COMMAND_EXECUTE_FAILURE) String commandId) {
		LOG.info("Command had an execution failure (" + commandId + ")");
	}

	@Inject
	@Optional
	public void commandPostExecuteSuccess(
			@EventTopic(CommandListenerEvents.TOPIC_COMMAND_POST_EXECUTE_SUCCESS) String commandId) {
		LOG.info("Command has been successfully executed (" + commandId + ")");
	}

	@Inject
	@Optional
	public void commandPreExecute(@EventTopic(CommandListenerEvents.TOPIC_COMMAND_PRE_EXECUTE) String commandId) {
		LOG.info("Command is about to be executed (" + commandId + ")");
	}

}
