package com.vogella.prioritizer.bugzilla.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.vogella.prioritizer.bugzilla.model.json.JSONBugzillaBug;
import com.vogella.prioritizer.server.issue.api.model.Attachment;
import com.vogella.prioritizer.server.issue.api.model.Bug;
import com.vogella.prioritizer.server.issue.api.model.Comment;

import lombok.Data;

public class BugzillaBug implements Bug {
	private int id;
	private String resolution;
	private boolean isOpen;
	private String status;
	private String creator;
	private String summary;
	private String platform;
	private String product;
	private String component;
	private String severity;
	private String priority;
	private String version;
	private Date creationTime;
	private List<String> cc;
	private List<Integer> blocks;
	private List<String> keywords;
	private List<String> seeAlso;
	private List<Comment> comments;
	private List<Attachment> attachments;
	private float userPriority;
	
	public static Bug of(JSONBugzillaBug jsonBugzillaBug) {
		BugzillaBug bugzillaBug = new BugzillaBug();
		
		bugzillaBug.setId(jsonBugzillaBug.getId());
		bugzillaBug.setBlocks(jsonBugzillaBug.getBlocks());
		bugzillaBug.setCc(jsonBugzillaBug.getCc());
		bugzillaBug.setComponent(jsonBugzillaBug.getComponent());
		bugzillaBug.setCreationTime(jsonBugzillaBug.getCreationTime());
		bugzillaBug.setCreator(jsonBugzillaBug.getCreator());
		bugzillaBug.setKeywords(jsonBugzillaBug.getKeywords());
		bugzillaBug.setOpen(jsonBugzillaBug.isOpen());
		bugzillaBug.setPlatform(jsonBugzillaBug.getPlatform());
		bugzillaBug.setPriority(jsonBugzillaBug.getPriority());
		bugzillaBug.setProduct(jsonBugzillaBug.getProduct());
		bugzillaBug.setResolution(jsonBugzillaBug.getResolution());
		bugzillaBug.setSeverity(jsonBugzillaBug.getSeverity());
		bugzillaBug.setStatus(jsonBugzillaBug.getStatus());
		bugzillaBug.setSummary(jsonBugzillaBug.getSummary());
		bugzillaBug.setVersion(jsonBugzillaBug.getVersion());
		bugzillaBug.setComments(Collections.emptyList());
		bugzillaBug.setAttachments(Collections.emptyList());
		bugzillaBug.setSeeAlso(jsonBugzillaBug.getSeeAlso());
		
		return bugzillaBug;
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

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
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

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
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

	public List<String> getSeeAlso() {
		return seeAlso;
	}

	public void setSeeAlso(List<String> seeAlso) {
		this.seeAlso = seeAlso;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public float getUserPriority() {
		return userPriority;
	}

	public void setUserPriority(float userPriority) {
		this.userPriority = userPriority;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BugzillaBug other = (BugzillaBug) obj;
		return id == other.id;
	}

	
}
