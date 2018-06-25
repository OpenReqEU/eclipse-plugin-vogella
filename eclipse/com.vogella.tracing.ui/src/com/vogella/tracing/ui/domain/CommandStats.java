package com.vogella.tracing.ui.domain;

public class CommandStats {

	private String commandId;
	private String commandName;
	private double invocations;
	private String keybinding;

	public CommandStats(String commandId, String commandName, double invocations, String keybinding) {
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

	public double getInvocations() {
		return invocations;
	}

	public void setInvocations(double invocations) {
		this.invocations = invocations;
	}

	public String getKeybinding() {
		return keybinding;
	}

	public void setKeybinding(String keybinding) {
		this.keybinding = keybinding;
	}
}
