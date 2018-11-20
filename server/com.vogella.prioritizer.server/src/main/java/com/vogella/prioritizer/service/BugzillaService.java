package com.vogella.prioritizer.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vogella.prioritizer.server.issue.api.IssueService;
import com.vogella.prioritizer.server.issue.api.model.Bug;

import reactor.core.publisher.Flux;

@Service
public class BugzillaService {

	@Autowired
	private IssueService issueApi;

	public Flux<Bug> getMostDiscussedBugsOfTheMonth(List<String> product, List<String> component) {

		LocalDate firstDayOfTheMonth = LocalDate.now().withDayOfMonth(1);
		Date lastModifiedDate = Date.from(firstDayOfTheMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		Flux<Bug> bugs = issueApi.getBugs(null, 500, product, component, null, null, lastModifiedDate, true);

		return bugs.sort((b1, b2) -> Integer.compare(b2.getComments().size(), b1.getComments().size()));
	}
}