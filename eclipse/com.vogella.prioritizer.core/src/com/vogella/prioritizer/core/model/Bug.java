package com.vogella.prioritizer.core.model;

import java.util.Date;
import java.util.List;

public class Bug {
	
	public static final Bug LOADING_DATA_FAKE_BUG = new Bug("Loading data...");
	
	private int id;
	private String resolution;
	private boolean is_open;
	private String status;
	private String creator;
	private String summary;
	private String platform;
	private String product;
	private String component;
	private String severity;

	public Bug() {
	}
	
	public Bug(String summary) {
		this.summary = summary;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getResolution() {
		return resolution;
	}

	public void setResolution(String resolution) {
		this.resolution = resolution;
	}

	public boolean isIs_open() {
		return is_open;
	}

	public void setIs_open(boolean is_open) {
		this.is_open = is_open;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getCreation_time() {
		return creation_time;
	}

	public void setCreation_time(Date creation_time) {
		this.creation_time = creation_time;
	}

	public List<String> getCc() {
		return cc;
	}

	public void setCc(List<String> cc) {
		this.cc = cc;
	}

	public List<Integer> getBlocks() {
		return blocks;
	}

	public void setBlocks(List<Integer> blocks) {
		this.blocks = blocks;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	private String priority;
	private String version;
	private Date creation_time;
	private List<String> cc;
	private List<Integer> blocks;
	private List<String> keywords;
}