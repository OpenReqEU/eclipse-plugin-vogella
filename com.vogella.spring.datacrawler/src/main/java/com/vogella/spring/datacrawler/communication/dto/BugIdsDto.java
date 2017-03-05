package com.vogella.spring.datacrawler.communication.dto;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class BugIdsDto {

	@ElementList(name="Seq", inline=false)
	@Path("result/bugs")
	private List<BugIdDto> bugIdDtos;

	public List<BugIdDto> getBugIdDtos() {
		return bugIdDtos;
	}

	public void setBugIdDtos(List<BugIdDto> bugIdDtos) {
		this.bugIdDtos = bugIdDtos;
	}
}
