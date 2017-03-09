package com.vogella.spring.datacrawler.communication.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import com.vogella.spring.datacrawler.data.entities.Bug;
import com.vogella.spring.datacrawler.data.entities.Comment;

import lombok.Data;

@Root(strict = false)
@Data
public class BugDto {

	@Element(name = "bug_id")
	private String id;

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

	@Element(name = "bug_status")
	private String status;

	@Element(name = "priority")
	private String priority;

	@Element(name = "bug_severity")
	private String severity;

	@Element(name = "votes")
	private int votes;
	
	@ElementList(entry = "cc", inline = true, required = false)
	Set<String> ccList;

	@ElementList(entry = "see_also", inline = true, required = false)
	Set<String> additionalLinks;

	@ElementList(entry = "long_desc", inline = true, required = false)
	List<CommentDto> commentDtos;

	public Bug getBugFromBugDto() {
		// TODO move
		Bug bug = new Bug();
		bug.setBugId(getId());
		bug.setProduct(getProduct());
		bug.setComponent(getComponent());
		bug.setReporter(getReporter());
		bug.setAssignedTo(getAssignedTo());
		bug.setCreationTimestamp(getCreationTimestamp());
		bug.setLastChangeTimestamp(getLastChangeTimestamp());
		bug.setPriority(getPriority());
		bug.setSeverity(getSeverity());
		bug.setStatus(getStatus());
		bug.setVersion(getVersion());
		bug.setVotes(getVotes());
		bug.setCcList(getCcList());
		bug.setAdditionalLinks(getAdditionalLinks());
		// TODO move
		if (getCommentDtos() != null) {
			List<Comment> comments = new ArrayList<>();
			getCommentDtos().forEach(commentDto -> {
				Comment comment = new Comment(commentDto.getCommentId(), commentDto.getCommentCount(),
						commentDto.getAuthor(), commentDto.getPublishTimestamp(), commentDto.getText(), bug);
				comments.add(comment);
			});
			bug.setComments(comments);
		}
		return bug;
	}
}
