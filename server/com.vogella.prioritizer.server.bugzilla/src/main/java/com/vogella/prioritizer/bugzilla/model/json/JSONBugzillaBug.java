package com.vogella.prioritizer.bugzilla.model.json;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class JSONBugzillaBug {
	private int id;
	private String resolution;
	@JsonAlias("is_open")
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
	@JsonAlias("creation_time")
	private Date creationTime;
	private List<String> cc;
	private List<Integer> blocks;
	private List<String> keywords;
}
