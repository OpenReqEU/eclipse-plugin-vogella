package com.vogella.spring.datacrawler.communication.dto;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict=false)
@Data
public class BugIdDto {
	
	@Element(name="id")
	@Path("bug")
	private int id;
}
