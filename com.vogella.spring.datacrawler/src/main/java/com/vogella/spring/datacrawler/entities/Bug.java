package com.vogella.spring.datacrawler.entities;

import java.util.Map;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "Bug")
@Data
public class Bug {

	@Id
	private int bugIdBugzilla;
	private String title;
	
//	@Column(length=65535)
//	private String description;
	
	private String classification;
	private String product;
	private String component;
	private String reporter;
	private String assignedTo;
	private long creationTimestamp;
	private long lastChangeTimestamp;
	private String version;
	private String reportedPlatform;
	private String operationSystem;
	private String status;
	private String resolution;
	private String priority;
	private String severity;
	private String milestone;
	private int votes;
	
	private int countCC;
	private int countComments;
	private int countAttachments;
	private int countDuplicates;
	private int countDependsOn;
	private int countBlocks;
	private int countAdditionalLinks;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "keywords")
	private Map<String, Integer> keywords;

//
//	@ElementCollection(fetch = FetchType.EAGER)
//	Set<String> additionalLinks;
//	
//	@OneToMany(mappedBy = "bug", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@JsonManagedReference
//	@OrderColumn
//	List<Comment> comments;
//
//	@OneToMany(mappedBy = "bug", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//	@JsonManagedReference
//	@OrderColumn
//	List<Attachment> attachments;
//	
//	@ElementCollection(fetch = FetchType.EAGER)
//	Set<String> dependsOn;
//
//	@ElementCollection(fetch = FetchType.EAGER)
//	Set<String> blocks;
}
