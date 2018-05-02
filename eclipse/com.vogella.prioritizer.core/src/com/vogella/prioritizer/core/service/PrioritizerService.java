package com.vogella.prioritizer.core.service;

import java.util.List;

import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.RankedBug;

import reactor.core.publisher.Mono;

public interface PrioritizerService {
	Mono<String> getKeyWordUrl(String assignee, List<String> product, List<String> component);

	Mono<List<RankedBug>> getSuitableBugs(String assignee, List<String> product, List<String> component);

	Mono<List<Bug>> getMostDiscussedBugsOfTheMonth(List<String> product, List<String> component);
}
