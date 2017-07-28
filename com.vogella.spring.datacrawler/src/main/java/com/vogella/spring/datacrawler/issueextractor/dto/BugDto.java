package com.vogella.spring.datacrawler.issueextractor.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict = false)
@Data
public class BugDto {

	@Element(name = "bug_id")
	private int bugDtoId;

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

	@ElementList(entry = "cc", inline = true, required = false)
	Set<String> ccList = new HashSet<>();

	@ElementList(entry = "see_also", inline = true, required = false)
	Set<String> additionalLinks = new HashSet<>();

	@ElementList(entry = "long_desc", inline = true, required = false)
	List<CommentDto> commentDtos = new ArrayList<>();

	@ElementList(entry = "attachment", inline = true, required = false)
	List<AttachmentDto> attachmentDtos = new ArrayList<>();

	@ElementList(entry = "dependson", inline = true, required = false)
	Set<String> dependsOn = new HashSet<>();

	@ElementList(entry = "blocks", inline = true, required = false)
	Set<String> blocks = new HashSet<>();
}
