package com.vogella.spring.datacrawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.vogella.spring.datacrawler.communication.BugzillaController;

@Service
public class Scheduler {
	
	@Autowired
	private BugzillaController controller;
	
	@Scheduled(cron = "0 0 23 ? * MON-FRI")
	public void loadNewBugs() {
		controller.loadBugs();
	}
}
