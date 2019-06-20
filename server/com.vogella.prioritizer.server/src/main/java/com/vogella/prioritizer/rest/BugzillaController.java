package com.vogella.prioritizer.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vogella.prioritizer.server.issue.api.model.Bug;
import com.vogella.prioritizer.service.BugzillaService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/bugzilla")
@Api("/bugzilla")
class BugzillaController {

	private BugzillaService prioritizerService;

	public BugzillaController(BugzillaService prioritizerService) {
		this.prioritizerService = prioritizerService;
	}

	@GetMapping("/mostDiscussedBugs")
	@ApiOperation(value = "Find most discussed bugs", notes = "Find most discussed bugs for a certain product and component. By default of the last 30 days.", response = Flux.class)
    @ApiResponses({
        @ApiResponse(code = 200, message = "Success", response = Bug.class)
    })
	public Flux<Bug> getMostDiscussedBugs(@RequestParam(name = "product", required = false, defaultValue="Platform") List<String> product,
			@RequestParam(name = "component", required = false, defaultValue="UI") List<String> component, @RequestParam(name= "daysBack", required=false, defaultValue="30") long daysBack) {
		System.out.println("Incomming request");
		return prioritizerService.getMostDiscussedBugs(product, component, daysBack);
	}
}
