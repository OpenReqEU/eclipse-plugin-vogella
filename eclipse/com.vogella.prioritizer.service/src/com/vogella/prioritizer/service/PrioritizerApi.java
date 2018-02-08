package com.vogella.prioritizer.service;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface PrioritizerApi {

	@GET("/getChart")
	@Streaming
	Single<ResponseBody> getKeywordImageBytes(@Query("assignee") String assignee, @Query("limit") int limit);
}
