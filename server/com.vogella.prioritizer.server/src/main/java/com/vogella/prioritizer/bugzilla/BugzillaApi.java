package com.vogella.prioritizer.bugzilla;

import com.vogella.prioritizer.bugzilla.model.BugResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BugzillaApi {

	String BASE_URL = "https://bugs.eclipse.org/bugs/";

	@GET("rest/bug")
	Single<BugResponse> getBugsOfAssignee(@Query("assigned_to") String assignee, @Query("limit") int limit, @Query("status") String status);

}