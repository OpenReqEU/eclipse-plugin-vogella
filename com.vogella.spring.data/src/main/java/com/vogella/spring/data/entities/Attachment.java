package com.vogella.spring.data.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "Attachment")
@Data
@ToString(exclude = "bug")
public class Attachment {

	public Attachment() {
	}

	public Attachment(String attachmentId, String createdTimestamp, String lastChangedTimestamp, String description,
			String filename, String type, String size, String attacher, String isObsolete, String isPatch, Bug bug) {
		super();
		this.attachmentId = attachmentId;
		this.createdTimestamp = createdTimestamp;
		this.lastChangedTimestamp = lastChangedTimestamp;
		this.description = description;
		this.filename = filename;
		this.type = type;
		this.size = size;
		this.attacher = attacher;
		this.isObsolete = isObsolete;
		this.isPatch = isPatch;
		this.bug = bug;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private String id;
	private String attachmentId;
	private String createdTimestamp;
	private String lastChangedTimestamp;
	private String description;
	private String filename;
	private String type;
	private String size;
	private String attacher;
	private String isObsolete;
	private String isPatch;

	@ManyToOne
	@JsonBackReference
	private Bug bug;
}
