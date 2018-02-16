package com.vogella.prioritizer.rest;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.prioritizer.server.issue.api.model.Bug;
import com.vogella.prioritizer.service.PrioritizerService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
class PrioritizerController {

	private PrioritizerService prioritizerService;

	public PrioritizerController(PrioritizerService prioritizerService) {
		this.prioritizerService = prioritizerService;
	}

	@GetMapping("/findSuitableBugs")
	public Flux<Bug> findSuitableBugs(@RequestParam("assignee") String assignee,
			@RequestParam("product") String product, @RequestParam("component") String component,
			@RequestParam(name = "limit", required = false, defaultValue = "50") int limit) {
		return prioritizerService.findSuitableBugs(assignee, product, component, limit);
	}

	@GetMapping(value = "/getChart", produces = MediaType.IMAGE_PNG_VALUE)
	public @ResponseBody Mono<byte[]> getKeywordImage(@RequestParam("assignee") String assignee,
			@RequestParam(name = "width", required = false) int width,
			@RequestParam(name = "height", required = false) int height,
			@RequestParam(name = "product", required = false) String product,
			@RequestParam(name = "component", required = false) String component,
			@RequestParam(name = "limit", required = false, defaultValue = "200") int limit) {
		return prioritizerService.getKeywordImage(assignee, width, height, product, component, limit);
	}
}
