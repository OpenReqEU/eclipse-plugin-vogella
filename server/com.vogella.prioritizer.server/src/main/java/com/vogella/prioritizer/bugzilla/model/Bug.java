package com.vogella.prioritizer.bugzilla.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bug {
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
	private String priority;
	private String version;
	private Date creation_time;
	private List<String> cc;
	private List<Integer> blocks;
	private List<String> keywords;
}
