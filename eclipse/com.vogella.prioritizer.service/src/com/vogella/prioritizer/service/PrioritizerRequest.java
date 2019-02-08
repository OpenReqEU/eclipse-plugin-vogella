package com.vogella.prioritizer.service;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PrioritizerRequest {

	private String agentID;
	private String assignee;
	private List<String> products;
	private List<String> components;
	private List<String> keywords;

	public String getAgent_id() {
		return agentID;
	}

	public void setAgent_id(String agentID) {
		this.agentID = agentID;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public List<String> getProducts() {
		return products;
	}

	public void setProducts(List<String> products) {
		this.products = products;
	}

	public List<String> getComponents() {
		return components;
	}

	public void setComponents(List<String> components) {
		this.components = components;
	}

	public List<String> getKeywords() {
		if(null == keywords) {
			return Collections.emptyList();
		}
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
}
