package com.vogella.spring.datacrawler.communication.dto;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict = false)
@Data
public class CommentDto {

	@Element(name="commentid")
	private String commentId;
	
	@Element(name="comment_count")
	private String commentCount;
	
	@Element(name="who")
	private String author;
	
	@Element(name="bug_when")
	private String publishTimestamp;
	
	@Element(name="thetext", required=false)
	private String text;	
}
