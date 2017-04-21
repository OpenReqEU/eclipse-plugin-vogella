package com.vogella.spring.datacrawler.communication.dto;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict=false)
@Data
public class BugsDto {
	
	@ElementList(entry="bug", inline=true)
	List<BugDto> bugDtos;
}
