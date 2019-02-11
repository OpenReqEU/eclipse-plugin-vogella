package com.vogella.prioritizer.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RankedBug implements Comparable<RankedBug> {

	public static final RankedBug LOADING_DATA_FAKE_BUG = new RankedBug("Loading data...");

	private long id;
	private String summary;
	private String url;
	private String product;
	private String component;
	private double priority;

	public RankedBug() {
	}

	private RankedBug(String summary) {
		this.summary = summary;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
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
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public int compareTo(RankedBug o) {
		if(null == o) {
			return 1;
		}
		return Double.compare(o.getPriority(), priority);
	}

	@Override
	public String toString() {
		return "RankedBug [id=" + id + ", url= " + url +", summary=" + summary + ", product=" + product + ", component=" + component
				+ ", priority=" + priority + "]";
	}
	
	
}
