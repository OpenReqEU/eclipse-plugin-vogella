package com.vogella.prioritizer.service;

import java.util.List;

import com.vogella.prioritizer.core.model.Bug;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface PrioritizerApi {

	@GET("/getChart")
	@Streaming
	Single<ResponseBody> getKeywordImageBytes(@Query("assignee") String assignee, @Query("limit") int limit);

	@GET("/findSuitableBugs")
	@Streaming
	Single<List<Bug>> getSuitableBugs(@Query("assignee") String assignee, @Query("limit") int limit);
}
