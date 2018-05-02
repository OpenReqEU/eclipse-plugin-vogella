package com.vogella.prioritizer.service;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vogella.prioritizer.core.model.RankedBug;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BugzillaPriorityResponse {
	private List<RankedBug> rankedBugs;
	private String errorMessage;
	private boolean error;

	public List<RankedBug> getRankedBugs() {
		return rankedBugs;
	}

	public void setRankedBugs(List<RankedBug> rankedBugs) {
		this.rankedBugs = rankedBugs;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
}
