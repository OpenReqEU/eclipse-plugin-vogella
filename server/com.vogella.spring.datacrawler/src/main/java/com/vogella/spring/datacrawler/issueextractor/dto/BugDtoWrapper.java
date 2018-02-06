package com.vogella.spring.datacrawler.issueextractor.dto;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict=false)
@Data
public class BugDtoWrapper {
	
	@ElementList(entry="bug", inline=true)
	List<BugDto> bugDtos;
}
