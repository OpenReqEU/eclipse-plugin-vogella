package com.vogella.spring.datacrawler.communication.dto;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class BugIdDto {
	
	@Element(name="id")
	@Path("bug")
	private String id;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
