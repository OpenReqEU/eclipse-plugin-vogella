package com.vogella.spring.datacrawler;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.spring.data.entities.Bug;
import com.vogella.spring.data.repositories.BugRepository;

@RestController
public class BugRestController {

	private static final Logger log = Logger.getLogger(BugRestController.class.getName());

	@Autowired 
	BugRepository bugRepository;
	
	@RequestMapping("/bugs")
	public List<Bug> getBugs(){
		log.log(Level.INFO, "GET bugs");
		List<Bug> bugs = new ArrayList<>();
		bugRepository.findAll().forEach(bugs::add);
		return bugs;
	}
}
