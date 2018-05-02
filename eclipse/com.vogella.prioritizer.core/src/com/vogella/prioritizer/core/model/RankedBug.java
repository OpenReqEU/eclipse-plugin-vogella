package com.vogella.prioritizer.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RankedBug implements Comparable<RankedBug> {

	public static final RankedBug LOADING_DATA_FAKE_BUG = new RankedBug("Loading data...");

	private int id;
	private String summary;
	private String product;
	private String component;
	private double priority;

	public RankedBug() {
	}

	private RankedBug(String summary) {
		this.summary = summary;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public double getPriority() {
		return priority;
	}

	public void setPriority(double priority) {
		this.priority = priority;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	@Override
	public int compareTo(RankedBug o) {
		return Double.compare(priority, o.getPriority());
	}
}
