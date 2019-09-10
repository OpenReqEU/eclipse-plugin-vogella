package com.vogella.prioritizer.bugzilla.model.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONBugResponse {
	List<JSONBugzillaBug> bugs;

	public List<JSONBugzillaBug> getBugs() {
		return bugs;
	}

	public void setBugs(List<JSONBugzillaBug> bugs) {
		this.bugs = bugs;
	}
	
	
}
