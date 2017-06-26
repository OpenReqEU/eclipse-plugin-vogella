package com.vogella.spring.datacrawler.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.vogella.spring.datacrawler.communication.dto.AttachmentDto;
import com.vogella.spring.datacrawler.communication.dto.BugDto;
import com.vogella.spring.datacrawler.communication.dto.CommentDto;
import com.vogella.spring.datacrawler.data.entities.Attachment;
import com.vogella.spring.datacrawler.data.entities.Bug;
import com.vogella.spring.datacrawler.data.entities.Comment;

public class DtoToJpaConverter {

	public static Bug getBugFromBugDto(BugDto bugdto) {
		Bug bug = new Bug();
		bug.setBugIdBugzilla(bugdto.getBugDtoId());
		bug.setTitle(bugdto.getTitle());
		bug.setClassification(bugdto.getClassification());
		bug.setProduct(bugdto.getProduct());
		bug.setComponent(bugdto.getComponent());
		bug.setReporter(bugdto.getReporter());
		bug.setAssignedTo(bugdto.getAssignedTo());
		bug.setCreationTimestamp(convertTimestamp(bugdto.getCreationTimestamp()));
		bug.setLastChangeTimestamp(convertTimestamp(bugdto.getLastChangeTimestamp()));
		bug.setResolution(bugdto.getResolution());
		bug.setPriority(bugdto.getPriority());
		bug.setSeverity(bugdto.getSeverity());
		bug.setStatus(bugdto.getStatus());
		bug.setVersion(bugdto.getVersion());
		bug.setReportedPlatform(bugdto.getReportedPlatform());
		bug.setOperationSystem(bugdto.getOperationSystem());
		bug.setMilestone(bugdto.getMilestone());
		bug.setVotes(bugdto.getVotes());
		bug.setDependsOn(bugdto.getDependsOn());
		bug.setBlocks(bugdto.getBlocks());
		bug.setCcList(bugdto.getCcList());
		bug.setAdditionalLinks(bugdto.getAdditionalLinks());

		bug.setComments(getComments(bugdto.getCommentDtos(), bug));
		bug.setAttachments(getAttachments(bugdto.getAttachmentDtos(), bug));

		return bug;
	}

	private static List<Attachment> getAttachments(List<AttachmentDto> attachmentDtos, Bug bug) {
		List<Attachment> attachments = new ArrayList<>();
		if (attachmentDtos != null) {
			attachmentDtos.forEach(attachmentDto -> {
				Attachment attachment = new Attachment(attachmentDto.getAttachmentId(),
						attachmentDto.getCreatedTimestamp(), attachmentDto.getLastChangedTimestamp(),
						attachmentDto.getDescription(), attachmentDto.getFilename(), attachmentDto.getType(),
						attachmentDto.getSize(), attachmentDto.getAttacher(), attachmentDto.getIsObsolete(),
						attachmentDto.getIsPatch(), bug);
				attachments.add(attachment);
			});
			bug.setAttachments(attachments);
		}
		return attachments;
	}

	private static List<Comment> getComments(List<CommentDto> commentDtos, Bug bug) {
		List<Comment> comments = new ArrayList<>();
		if (commentDtos != null) {
			commentDtos.forEach(commentDto -> {
				Comment comment = new Comment(commentDto.getCommentId(), commentDto.getCommentCount(),
						commentDto.getAuthor(), commentDto.getPublishTimestamp(), commentDto.getText(), bug);
				comments.add(comment);
			});
		}
		return comments;
	}

	private static long convertTimestamp(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(dateString));
			return calendar.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0l;
	}
}
