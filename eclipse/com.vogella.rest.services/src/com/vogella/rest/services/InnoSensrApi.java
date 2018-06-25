package com.vogella.rest.services;

import reactor.core.publisher.Mono;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface InnoSensrApi {

	@POST("/api/v1/project/create")
	@Headers("Content-Type: application/json")
	Mono<Void> createProject(String title, String description);

	@POST("/api/v1/requirement/create")
	@Headers("Content-Type: application/json")
	Mono<Void> createRequirement(@Body InnoSensrRequirement requirement);
}
