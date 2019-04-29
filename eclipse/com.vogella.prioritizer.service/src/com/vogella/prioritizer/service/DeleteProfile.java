package com.vogella.prioritizer.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteProfile {
	private final String agentId;

	public DeleteProfile(String agentId) {
		this.agentId = agentId;
	}

	@JsonProperty("agent_id")
	public String getAgentId() {
		return agentId;
	}
}
