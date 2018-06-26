package com.vogella.tracing.ui.domain;

public class CommandStats {

	private String commandId;
	private String commandName;
	private int invocations;
	private String keybinding;

	public CommandStats(String commandId, String commandName, int invocations, String keybinding) {
		super();
		this.commandId = commandId;
		this.commandName = commandName;
		this.invocations = invocations;
		this.keybinding = keybinding;
	}

	public String getCommandId() {
		return commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public int getInvocations() {
		return invocations;
	}

	public void setInvocations(int invocations) {
		this.invocations = invocations;
	}

	public String getKeybinding() {
		return keybinding;
	}

	public void setKeybinding(String keybinding) {
		this.keybinding = keybinding;
	}
}
