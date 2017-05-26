package com.vogella.spring.datacrawler.communication;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

import com.vogella.spring.datacrawler.communication.dto.BugIdsDto;

public class BugIdsConverter implements Converter<BugIdsDto> {
	@Override
	public BugIdsDto read(InputNode node) throws Exception {
		BugIdsDto bugIdDtoWrapper = new BugIdsDto();
		List<Integer> list = new ArrayList<Integer>();

		for (InputNode currentNode = node; currentNode != null; currentNode = node.getNext()) {
			if(currentNode.getName().equals("id")) {
				list.add(Integer.valueOf(currentNode.getValue()));
			}
		}

		bugIdDtoWrapper.setBugIds(list);
		return bugIdDtoWrapper;
	}

	@Override
	public void write(OutputNode node, BugIdsDto value) throws Exception {

	}
}
