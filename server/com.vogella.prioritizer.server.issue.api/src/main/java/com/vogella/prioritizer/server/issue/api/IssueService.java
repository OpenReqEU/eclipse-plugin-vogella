package com.vogella.prioritizer.server.issue.api;

import java.util.Date;
import java.util.List;

import com.vogella.prioritizer.server.issue.api.model.Bug;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IssueService {

	Flux<Bug> getBugs(String assignee, long limit, List<String> product, List<String> component, String status, Date creationTime,
			Date lastChangeTime, boolean withComments);

	Mono<Bug> getBugById(int id);
}
