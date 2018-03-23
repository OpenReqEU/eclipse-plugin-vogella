package com.vogella.tracing.core.event;

public interface CommandListenerEvents {

	static final String TOPIC_COMMAND_NOT_HANDLED = "vogella/tracing/command/notHandled";
	static final String TOPIC_COMMAND_EXECUTE_FAILURE = "vogella/tracing/command/failure";
	static final String TOPIC_COMMAND_POST_EXECUTE_SUCCESS = "vogella/tracing/command/success";
	static final String TOPIC_COMMAND_PRE_EXECUTE = "vogella/tracing/command/pre";
}
