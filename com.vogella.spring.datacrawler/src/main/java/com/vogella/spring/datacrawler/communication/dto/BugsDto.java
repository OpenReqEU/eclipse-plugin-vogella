package com.vogella.spring.datacrawler.communication.dto;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(strict=false)
public class BugsDto {
	
	@ElementList(entry="bug", inline=true)
	List<BugDto> bugDtos;

	public List<BugDto> getBugDtos() {
		return bugDtos;
	}

	public void setBugDtos(List<BugDto> bugDtos) {
		this.bugDtos = bugDtos;
	}
}
