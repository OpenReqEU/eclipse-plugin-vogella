package com.vogella.common.core.domain;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BugProduct {
	private long id;
	private String name;
	private List<BugComponent> components;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BugComponent> getComponents() {
		return components;
	}

	public void setComponents(List<BugComponent> components) {
		this.components = components;
	}
}
