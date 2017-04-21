package com.vogella.spring.datacrawler.communication.dto;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict = false)
@Data
public class AttachmentDto {

	@Element(name = "attachid")
	private String attachmentId;

	@Element(name = "date")
	private String createdTimestamp;

	@Element(name = "delta_ts")
	private String lastChangedTimestamp;

	@Element(name = "desc")
	private String description;

	@Element(name = "filename")
	private String filename;

	@Element(name = "type")
	private String type;

	@Element(name = "size")
	private String size;

	@Element(name = "attacher")
	private String attacher;

	@Attribute(name = "isobsolete")
	private String isObsolete;

	@Attribute(name = "ispatch")
	private String isPatch;
}
