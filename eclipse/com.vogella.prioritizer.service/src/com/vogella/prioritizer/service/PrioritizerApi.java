package com.vogella.prioritizer.service;

import java.util.List;

import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.BugzillaPriorityResponse;

import reactor.core.publisher.Mono;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PrioritizerApi {

	@POST("/prioritizer/chart")
	@Headers("Content-Type: application/json")
	Mono<KeyWordUrlResponse> getKeyWordUrl(@Body PrioritizerRequest bugzillaRequest);

	@POST("/prioritizer/compute")
	@Headers("Content-Type: application/json")
	Mono<BugzillaPriorityResponse> getSuitableBugs(@Body PrioritizerRequest bugzillaRequest);

	@POST("/prioritizer/dislike")
	@Headers("Content-Type: application/json")
	Mono<BugzillaPriorityResponse> dislikeBug(@Body PrioritizerIdRequest bugzillaRequest);

	@POST("/prioritizer/like")
	@Headers("Content-Type: application/json")
	Mono<BugzillaPriorityResponse> likeBug(@Body PrioritizerIdRequest bugzillaRequest);

	@POST("/prioritizer/unlike")
	@Headers("Content-Type: application/json")
	Mono<BugzillaPriorityResponse> unlikeBug(@Body PrioritizerIdRequest bugzillaRequest);

	@POST("/prioritizer/defer")
	@Headers("Content-Type: application/json")
	Mono<BugzillaPriorityResponse> deferBug(@Body PrioritizerIdIntervalRequest bugzillaRequest);

	@POST("/prioritizer/profile/delete")
	@Headers("Content-Type: application/json")
	Mono<BugzillaPriorityResponse> deleteProfile(@Body DeleteProfile bugzillaRequest);

	@GET("http://localhost:9801/bugzilla/mostDiscussedBugs")
	Mono<List<Bug>> getMostDiscussedBugs(@Query("product") List<String> product,
			@Query("component") List<String> component, @Query("daysBack") long daysBack);
}
