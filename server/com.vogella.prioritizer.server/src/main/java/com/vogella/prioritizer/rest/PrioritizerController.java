package com.vogella.prioritizer.rest;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.prioritizer.bugzilla.model.Bug;
import com.vogella.prioritizer.service.PrioritizerService;

@RestController
class PrioritizerController {

	@Autowired
	private PrioritizerService prioritizerService;

	@GetMapping("/findSuitableBugs")
	public List<Bug> findSuitableBugs(@RequestParam("assignee") String assignee,
			@RequestParam(name = "limit", required = false, defaultValue = "50") int limit) throws IOException {
		return prioritizerService.findSuitableBugs(assignee, limit);
	}

	@GetMapping(value = "/getChart", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody byte[] getKeywordImage(@RequestParam("assignee") String assignee,
			@RequestParam(name = "limit", required = false, defaultValue = "200") int limit) throws IOException {
		return prioritizerService.getKeywordImage(assignee, limit);
	}
}
