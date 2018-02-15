package com.vogella.prioritizer.bugzilla.model;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.vogella.prioritizer.bugzilla.model.json.JSONBugzillaBug;
import com.vogella.prioritizer.server.issue.api.model.Attachment;
import com.vogella.prioritizer.server.issue.api.model.Bug;
import com.vogella.prioritizer.server.issue.api.model.Comment;

import lombok.Data;

@Data
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
	private List<Comment> comments;
	private List<Attachment> attachments;
	
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
		
		return bugzillaBug;
	}
}
