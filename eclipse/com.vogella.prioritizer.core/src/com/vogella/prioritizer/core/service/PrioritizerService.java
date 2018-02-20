package com.vogella.prioritizer.core.service;

import java.util.List;

import com.vogella.prioritizer.core.model.PriorityBug;

import reactor.core.publisher.Mono;

public interface PrioritizerService {
	public Mono<byte[]> getKeyWordImage(String assignee,int width, int height, String product, String component, int limit);

	public Mono<List<PriorityBug>> getSuitableBugs(String assignee, String product, String component, int limit);
}
