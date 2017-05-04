package com.vogella.spring.datacrawler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.spring.data.entities.Bug;
import com.vogella.spring.data.repositories.BugRepository;

import lombok.extern.java.Log;

@RestController
@Log
public class BugRestController {
	
	@Autowired 
	BugRepository bugRepository;
	
	@RequestMapping("/bugs")
	public List<Bug> getBugs(){
		log.log(Level.INFO, "GET: bugs");
		List<Bug> bugs = new ArrayList<>();
		bugRepository.findAll().forEach(bugs::add);
		return bugs;
	}
}
