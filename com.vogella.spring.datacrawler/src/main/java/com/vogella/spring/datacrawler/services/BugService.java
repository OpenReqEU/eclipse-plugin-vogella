package com.vogella.spring.datacrawler.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vogella.spring.datacrawler.entities.Bug;
import com.vogella.spring.datacrawler.repositories.BugRepository;

@Service("bugService")
public class BugService {

	@Autowired
	private BugRepository bugRepository;

	public Map<String, Integer> findAllKeywordsForUser(String user) {
		List<Bug> bugs = bugRepository.findByAssignedTo(user);
		Map<String, Integer> keywords = new HashMap<String, Integer>();
		bugs.forEach(bug -> {
			bug.getKeywords().forEach((k, newFrequency) -> {
				if (keywords.containsKey(k)) {
					keywords.put(k, keywords.get(k) + newFrequency);
				} else {
					keywords.put(k, newFrequency);
				}
			});
		});
		return keywords;
	}
}
