package com.vogella.rest.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InnoSensrRequirement {
	private String projectUniqueKey;
	private String title;
	private String description;
	private String status;

	public InnoSensrRequirement() {
	}

	public InnoSensrRequirement(String projectUniqueKey, String title, String description, String status) {
		super();
		this.projectUniqueKey = projectUniqueKey;
		this.title = title;
		this.description = description;
		this.status = status;
	}

	public String getProjectUniqueKey() {
		return projectUniqueKey;
	}

	public void setProjectUniqueKey(String projectUniqueKey) {
		this.projectUniqueKey = projectUniqueKey;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
