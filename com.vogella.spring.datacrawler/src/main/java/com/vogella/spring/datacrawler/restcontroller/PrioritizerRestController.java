package com.vogella.spring.datacrawler.restcontroller;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.spring.data.entities.Model;
import com.vogella.spring.data.entities.RankedBug;
import com.vogella.spring.data.entities.UserAccount;
import com.vogella.spring.datacrawler.fileexporter.ArffFileExporter;
import com.vogella.spring.datacrawler.services.BugService;

@RestController
public class PrioritizerRestController {

	private static final Logger log = Logger.getLogger(PrioritizerRestController.class.getName());

	@Autowired 
	BugService bugService;
	
	@Autowired
	ArffFileExporter fileExporter;

	@RequestMapping(value = "/export")
	public HttpStatus exportData() {
		log.log(Level.INFO, "Export data");
		fileExporter.exportData();
		return HttpStatus.OK;
	}

	@RequestMapping(value = "/bugs")
	public List<RankedBug> getBugs() {
		log.log(Level.INFO, "GET bugs");
		return bugService.getRankedIssues();
	}

	@RequestMapping(value = "/user", method = RequestMethod.POST)
	public Model postUser(@RequestBody UserAccount userAccount) {
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return new Model();
	}
}
