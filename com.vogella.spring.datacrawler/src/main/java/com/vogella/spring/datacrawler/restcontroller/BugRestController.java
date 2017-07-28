package com.vogella.spring.datacrawler.restcontroller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.spring.data.entities.RankedBug;
import com.vogella.spring.datacrawler.entities.Bug;
import com.vogella.spring.datacrawler.repositories.BugRepository;

@RestController
public class BugRestController {

	private static final Logger log = Logger.getLogger(BugRestController.class.getName());

	@Autowired 
	BugRepository bugRepository;
	
	@RequestMapping("/bugs")
	public List<RankedBug> getBugs() {
		log.log(Level.INFO, "GET bugs");
		List<Bug> bugs = new ArrayList<>();
		bugRepository.findAll().forEach(bugs::add);
		return getRankedBugs(bugs);
	}

	private List<RankedBug> getRankedBugs(List<Bug> bugs) {
		List<RankedBug> rankedBugs = new ArrayList<>();
		bugs.forEach(bug -> {
			RankedBug rankedBug = new RankedBug();
			rankedBug.setBugIdBugzilla(bug.getBugIdBugzilla());
			rankedBug.setAssignedTo(bug.getAssignedTo());
			rankedBug.setComponent(bug.getComponent());
			rankedBug.setPriority(bug.getPriority());
			rankedBug.setReporter(bug.getReporter());
			rankedBug.setSeverity(bug.getSeverity());
			rankedBug.setStatus(bug.getStatus());
			rankedBug.setTitle(bug.getTitle());
			rankedBug.setVotes(bug.getVotes());
			rankedBug.setCreated(convertTimestamp(bug.getCreationTimestamp()));
			rankedBug.setLastChanged(convertTimestamp(bug.getLastChangeTimestamp()));
			rankedBug.setCountAttachments(bug.getCountAttachments());
			rankedBug.setCountBlocks(bug.getCountBlocks());
			rankedBug.setCountCC(bug.getCountCC());
			rankedBug.setCountDependsOn(bug.getCountDependsOn());
			rankedBug.setCountDuplicates(bug.getCountDuplicates());
			rankedBugs.add(rankedBug);
		});

		return rankedBugs;
	}

	private String convertTimestamp(long millis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return sdf.format(new Date(calendar.getTimeInMillis()));
	}
}
