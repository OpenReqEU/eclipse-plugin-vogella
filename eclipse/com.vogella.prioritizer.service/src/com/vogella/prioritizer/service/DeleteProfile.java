package com.vogella.prioritizer.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteProfile {
	private final String agentID;

	public DeleteProfile(String agentID) {
		this.agentID = agentID;
	}

	@JsonProperty("agent_id")
	public String getAgentId() {
		return agentID;
	}
}
