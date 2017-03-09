package com.vogella.spring.datacrawler.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name="Comment")
@Data
@ToString(exclude = "bug")
public class Comment {
	
	public Comment() {}
	
	public Comment( String commentId, String commentCount, String author, String publishTimestamp, String text,
			Bug bug) {
		super();
		this.commentId = commentId;
		this.commentCount = commentCount;
		this.author = author;
		this.publishTimestamp = publishTimestamp;
//		this.text = text;
		this.bug = bug;
	}

	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;

	private String commentId;
	
	private String commentCount;
	
	private String author;
	
	private String publishTimestamp;
	
//	@Column(length=65535)
//	private String text;
	
	@ManyToOne
	private Bug bug;

}
