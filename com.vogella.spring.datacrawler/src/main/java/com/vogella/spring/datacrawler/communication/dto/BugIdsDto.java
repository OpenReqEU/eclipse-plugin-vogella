package com.vogella.spring.datacrawler.communication.dto;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Path;
import org.simpleframework.xml.Root;

import lombok.Data;

@Root(strict=false)
@Data
public class BugIdsDto {

	@ElementList(name="Seq", inline=false)
	@Path("result/bugs")
	private List<BugIdDto> bugIdDtos;
}
