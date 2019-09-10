package com.vogella.prioritizer;

import org.springframework.scheduling.annotation.Scheduled;

import reactor.core.publisher.Mono;

public class ScheduledBugCollector {

	@Scheduled(cron="")
	public Mono<Void> gatherBugs() {
		return null;
	}
}
