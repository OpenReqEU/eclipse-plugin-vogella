package com.vogella.prioritizer.service;

public class DeleteProfile {
	private final String agentID;

	public DeleteProfile(String agentID) {
		this.agentID = agentID;
	}

	public String getAgentID() {
		return agentID;
	}
}
