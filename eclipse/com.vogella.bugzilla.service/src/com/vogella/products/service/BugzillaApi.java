package com.vogella.products.service;

import java.util.Date;
import java.util.List;

import com.vogella.common.core.domain.BugProduct;

import okhttp3.ResponseBody;
import reactor.core.publisher.Mono;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BugzillaApi {

	String BASE_URL = "https://bugs.eclipse.org/bugs/";

	@GET("rest/bug/{bugId}")
	Mono<ResponseBody> getBugById(@Path("bugId") long bugId);

	@GET("rest/bug")
	Mono<ResponseBody> getBugs(@Query("assigned_to") String assignee, @Query("product") List<String> product,
			@Query("component") List<String> component, @Query("limit") long limit, @Query("status") String status,
			@Query("creation_time") Date creationTime, @Query("last_change_time") Date lastChangeTime);

	@GET("rest/bug/{bugId}/comment")
	Mono<ResponseBody> getComments(@Path("bugId") long bugId);

	@GET("rest/bug/{bugId}/attachment")
	Mono<ResponseBody> getAttachments(@Path("bugId") long bugId);

	@GET("rest/product?type=accessible")
	Mono<List<BugProduct>> getProducts();
}