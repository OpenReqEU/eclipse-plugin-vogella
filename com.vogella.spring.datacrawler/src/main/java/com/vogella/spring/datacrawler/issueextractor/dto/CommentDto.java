package com.vogella.spring.datacrawler.issueextractor.dto;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict = false)
@Data
public class CommentDto {

	@Element(name="commentid")
	private int commentId;
	
	@Element(name="comment_count")
	private int commentCount;
	
	@Element(name="who")
	private String author;
	
	@Element(name="bug_when")
	private String publishTimestamp;
	
	@Element(name="thetext", required=false)
	private String text = "";	
}
