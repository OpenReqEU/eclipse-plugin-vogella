package com.vogella.spring.datacrawler.issueextractor.dto;

import java.util.List;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.convert.Convert;

import com.vogella.spring.datacrawler.issueextractor.BugIdsConverter;

import lombok.Data;

@Root(strict = false)
@Data
@Convert(value = BugIdsConverter.class)
public class BugIdsDto {
	private List<Integer> bugIds;
}