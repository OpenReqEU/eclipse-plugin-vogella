package com.vogella.spring.datacrawler.communication.dto;

import java.util.Set;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.vogella.spring.data.entities.Bug;

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
	Set<String> ccList;

	@ElementList(entry = "see_also", inline = true, required = false)
	Set<String> additionalLinks;

//	@ElementList(entry = "long_desc", inline = true, required = false)
//	List<CommentDto> commentDtos;
//
//	@ElementList(entry = "attachment", inline = true, required = false)
//	List<AttachmentDto> attachmentDtos;

	@ElementList(entry = "dependson", inline = true, required = false)
	Set<String> dependsOn;

	@ElementList(entry = "blocks", inline = true, required = false)
	Set<String> blocks;

	public Bug getBugFromBugDto() {
		// TODO move
		Bug bug = new Bug();
		bug.setBugIdBugzilla(getBugDtoId());
		bug.setTitle(getTitle());
		bug.setClassification(getClassification());
		bug.setProduct(getProduct());
		bug.setComponent(getComponent());
		bug.setReporter(getReporter());
		bug.setAssignedTo(getAssignedTo());
		bug.setCreationTimestamp(getCreationTimestamp());
		bug.setLastChangeTimestamp(getLastChangeTimestamp());
		bug.setResolution(getResolution());
		bug.setPriority(getPriority());
		bug.setSeverity(getSeverity());
		bug.setStatus(getStatus());
		bug.setVersion(getVersion());
		bug.setReportedPlatform(getReportedPlatform());
		bug.setOperationSystem(getOperationSystem());
		bug.setMilestone(getMilestone());
		bug.setVotes(getVotes());
		bug.setDependsOn(getDependsOn());
		bug.setBlocks(getBlocks());
		bug.setCcList(getCcList());
		bug.setAdditionalLinks(getAdditionalLinks());

//		// TODO move
//		if (getCommentDtos() != null) {
//			List<Comment> comments = new ArrayList<>();
//			getCommentDtos().forEach(commentDto -> {
//				Comment comment = new Comment(commentDto.getCommentId(), commentDto.getCommentCount(),
//						commentDto.getAuthor(), commentDto.getPublishTimestamp(), "", bug);
//				comments.add(comment);
//			});
//			bug.setComments(comments);
//		}
//
//		if (getAttachmentDtos() != null) {
//			List<Attachment> attachments = new ArrayList<>();
//			getAttachmentDtos().forEach(attachmentDto -> {
//				Attachment attachment = new Attachment(attachmentDto.getAttachmentId(),
//						attachmentDto.getCreatedTimestamp(), attachmentDto.getLastChangedTimestamp(),
//						attachmentDto.getDescription(), attachmentDto.getFilename(), attachmentDto.getType(),
//						attachmentDto.getSize(), attachmentDto.getAttacher(), attachmentDto.getIsObsolete(),
//						attachmentDto.getIsPatch(), bug);
//				attachments.add(attachment);
//			});
//			bug.setAttachments(attachments);
//		}
		return bug;
	}
}
