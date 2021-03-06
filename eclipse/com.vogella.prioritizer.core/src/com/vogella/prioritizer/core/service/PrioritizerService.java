package com.vogella.prioritizer.core.service;

import java.util.List;

import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.BugzillaPriorityResponse;
import com.vogella.prioritizer.core.model.RankedBug;

import reactor.core.publisher.Mono;

public interface PrioritizerService {
	Mono<String> getKeyWordUrl(String agentID, String assignee, List<String> product, List<String> component,
			List<String> keywords);

	Mono<List<RankedBug>> getSuitableBugs(String agentID, String assignee, List<String> product, List<String> component,
			List<String> keywords);

	Mono<BugzillaPriorityResponse> dislikeBug(String agentID, long bugId, String assignee, List<String> product,
			List<String> component);

	Mono<BugzillaPriorityResponse> likeBug(String agentID, long bugId, String assignee, List<String> product,
			List<String> component);

	Mono<BugzillaPriorityResponse> unlikeBug(String agentID, long bugId, String assignee, List<String> product,
			List<String> component);

	Mono<BugzillaPriorityResponse> deferBug(String agentID, long bugId, int interval, String assignee,
			List<String> product, List<String> component);

	Mono<List<Bug>> getMostDiscussedBugs(List<String> product, List<String> component, long daysBack, String status);

	Mono<BugzillaPriorityResponse> deleteProfile(String agentID);

}
