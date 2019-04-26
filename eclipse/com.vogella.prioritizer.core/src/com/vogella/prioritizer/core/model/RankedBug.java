package com.vogella.prioritizer.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RankedBug implements Comparable<RankedBug> {

	public static final RankedBug LOADING_DATA_FAKE_BUG = new RankedBug("Loading data...");

	private long id;
	private String summary;
	private String url;
	private String product;
	private String component;
	@JsonProperty("creation_time")
	private String creationTime;
	private boolean liked;
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
	public String getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(String creation_time) {
		this.creationTime = creation_time;
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

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	@Override
	public String toString() {
		return "RankedBug [id=" + id + ", summary=" + summary + ", url=" + url + ", product=" + product + ", component="
				+ component + ", created=" + creationTime + ", liked=" + liked + ", priority=" + priority + "]";
	}
}
