package com.vogella.common.core.domain;

public class CommandStats {
	
	public static final String NO_KEYBINDING_DEFINED = "No keybinding definied";

	private String commandId;
	private String commandName;
	private int invocations;
	private int menuDepth;
	private String keybinding;
	
	public CommandStats() {
	}

	public CommandStats(String commandId, String commandName, int invocations,int menuDepth, String keybinding) {
		this.commandId = commandId;
		this.commandName = commandName;
		this.invocations = invocations;
		this.menuDepth = menuDepth;
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

	public int getMenuDepth() {
		return menuDepth;
	}

	public void setMenuDepth(int menuDepth) {
		this.menuDepth = menuDepth;
	}

	public String getKeybinding() {
		return keybinding;
	}

	public void setKeybinding(String keybinding) {
		this.keybinding = keybinding;
	}
	
	public void incrementInvocations() {
		this.invocations++;
	}
}
