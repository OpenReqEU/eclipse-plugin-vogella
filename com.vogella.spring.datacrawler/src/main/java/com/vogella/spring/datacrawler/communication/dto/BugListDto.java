package com.vogella.spring.datacrawler.communication.dto;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(name="rdf", strict=false)
public class BugListDto {

	@ElementList(name="Seq", inline=false)
	@Path("result/bugs")
	private List<BugDto> bugList;

	public List<BugDto> getBugList() {
		return bugList;
	}

	public void setBugList(List<BugDto> bugList) {
		this.bugList = bugList;
	}
}
