package com.vogella.prioritizer.service;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteProfile {
	private final String agentId;

	public DeleteProfile(String agentId) {
		this.agentId = agentId;
	}

	@JsonProperty("agentID")
	public String getAgentId() {
		return agentId;
	}
}
