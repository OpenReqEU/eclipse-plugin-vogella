package com.vogella.prioritizer.bugzilla;

import com.vogella.prioritizer.bugzilla.model.BugResponse;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BugzillaApi {

	String BASE_URL = "https://bugs.eclipse.org/bugs/";

	@GET("rest/bug?status=NEW")
	Single<BugResponse> getRecentOpenBugs(@Query("limit") int limit);
	
	@GET("rest/bug")
	Single<BugResponse> getBugsOfAssignee(@Query("assigned_to") String assignee, @Query("limit") int limit, @Query("status") String status);

	@GET("rest/bug/{bugId}/comment")
	Single<ResponseBody> getComments(@Path("bugId") int bugId);
}