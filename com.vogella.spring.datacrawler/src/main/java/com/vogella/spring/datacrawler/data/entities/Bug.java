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

import org.simpleframework.xml.Element;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Table(name = "Bug")
@Data
public class Bug {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String bugId;

	@Element(name = "short_desc")
	private String title;

	@Element(name = "classification")
	private String classification;

	@Element(name = "product")
	private String product;

	@Element(name = "component")
	private String component;

	@Element(name = "reporter")
	private String reporter;

	@Element(name = "assigned_to")
	private String assignedTo;

	@Element(name = "creation_ts")
	private String creationTimestamp;

	@Element(name = "delta_ts")
	private String lastChangeTimestamp;

	@Element(name = "version")
	private String version;

	@Element(name = "rep_platform")
	private String reportedPlatform;

	@Element(name = "op_sys")
	private String operationSystem;

	@Element(name = "bug_status")
	private String status;

	@Element(name = "resolution", required = false)
	private String resolution;

	@Element(name = "priority")
	private String priority;

	@Element(name = "bug_severity")
	private String severity;

	@Element(name = "target_milestone")
	private String milestone;

	@Element(name = "votes")
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
