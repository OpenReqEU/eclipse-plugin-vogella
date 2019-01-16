package com.vogella.prioritizer.core.service;

import java.util.List;

import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.RankedBug;

import reactor.core.publisher.Mono;

public interface PrioritizerService {
	Mono<String> getKeyWordUrl(String agentID, String assignee, List<String> product, List<String> component);

	Mono<List<RankedBug>> getSuitableBugs(String agentID, String assignee, List<String> product,
			List<String> component);

	Mono<Void> dislikeBug(String agentID, long bugId, String assignee, List<String> product, List<String> component);

	Mono<Void> likeBug(String agentID, long bugId, String assignee, List<String> product, List<String> component);

	Mono<Void> deferBug(String agentID, long bugId, int interval, String assignee, List<String> product,
			List<String> component);

	Mono<List<Bug>> getMostDiscussedBugs(List<String> product, List<String> component, long daysBack);
}
