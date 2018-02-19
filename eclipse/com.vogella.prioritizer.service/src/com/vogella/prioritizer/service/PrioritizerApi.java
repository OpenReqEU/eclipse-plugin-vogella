package com.vogella.prioritizer.service;

import java.util.List;

import com.vogella.prioritizer.core.model.PriorityBug;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface PrioritizerApi {

	@GET("/getChart")
	@Streaming
	Single<ResponseBody> getKeywordImageBytes(@Query("assignee") String assignee, @Query("width") int width,
			@Query("height") int height, @Query("product") String product, @Query("component") String component,
			@Query("limit") int limit);

	@GET("/findSuitableBugs")
	Single<List<PriorityBug>> getSuitableBugs(@Query("assignee") String assignee, @Query("product") String product,
			@Query("component") String component, @Query("limit") int limit);
}
