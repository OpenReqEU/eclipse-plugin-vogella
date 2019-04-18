package com.vogella.prioritizer.service;

public class DeleteProfile {
	private final String agentId;

	public DeleteProfile(String agentId) {
		this.agentId = agentId;
	}

	public String getAgentId() {
		return agentId;
	}
}
