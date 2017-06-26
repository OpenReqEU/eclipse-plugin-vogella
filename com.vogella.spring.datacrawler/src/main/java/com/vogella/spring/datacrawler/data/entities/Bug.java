package com.vogella.spring.datacrawler.data.entities;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Table(name = "Bug")
@Data
public class Bug {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int bugIdInternal;
	private int bugIdBugzilla;
	private String title;
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

	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> ccList;

	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> additionalLinks;
	
	@OneToMany(mappedBy = "bug", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference
	@OrderColumn
	List<Comment> comments;

	@OneToMany(mappedBy = "bug", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JsonManagedReference
	@OrderColumn
	List<Attachment> attachments;
	
	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> dependsOn;

	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> blocks;
}
