package com.vogella.prioritizer.bugzilla.model;


import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vogella.prioritizer.server.issue.api.model.Comment;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BugzillaComment implements Comment {
	Date time;
	String text;
	long bugId;
	long count;
	long attachmentId;
	boolean isPrivate;
	boolean isMarkdown;
	List<String> tags;
	String creator;
	Date creationTime;
	long id;
}
