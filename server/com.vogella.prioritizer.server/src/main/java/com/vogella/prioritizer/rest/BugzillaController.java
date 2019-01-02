package com.vogella.prioritizer.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.prioritizer.server.issue.api.model.Bug;
import com.vogella.prioritizer.service.BugzillaService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/bugzilla")
class BugzillaController {

	private BugzillaService prioritizerService;

	public BugzillaController(BugzillaService prioritizerService) {
		this.prioritizerService = prioritizerService;
	}

	@GetMapping("/mostDiscussedBugs")
	public Flux<Bug> getMostDiscussedBugs(@RequestParam(name = "product", required = false, defaultValue="Platform") List<String> product,
			@RequestParam(name = "component", required = false, defaultValue="UI") List<String> component, @RequestParam(name= "daysBack", required=false, defaultValue="30") long daysBack) {
		return prioritizerService.getMostDiscussedBugs(product, component, daysBack);
	}
}
