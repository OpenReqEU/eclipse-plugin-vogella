package com.vogella.prioritizer.service;

import java.util.List;

import com.vogella.prioritizer.core.model.Bug;

import reactor.core.publisher.Mono;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PrioritizerApi {

	@POST("/prioritizer/chart")
	@Headers("Content-Type: application/json")
	Mono<KeyWordUrlResponse> getKeyWordUrl(@Body BugzillaRequest bugzillaRequest);

	@POST("/prioritizer/compute")
	@Headers("Content-Type: application/json")
	Mono<BugzillaPriorityResponse> getSuitableBugs(@Body BugzillaRequest bugzillaRequest);

	@GET("/mostDiscussedBugsOfTheMonth")
	Mono<List<Bug>> getMostDiscussedBugsOfTheMonth(@Query("product") List<String> product,
			@Query("component") List<String> component);
}
