package com.vogella.prioritizer.bugzilla;

import java.util.Date;
import java.util.List;

import com.vogella.prioritizer.bugzilla.model.json.JSONBugResponse;

import okhttp3.ResponseBody;
import reactor.core.publisher.Mono;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

@SuppressWarnings("squid:S1214")
public interface BugzillaApi {

	String BASE_URL = "https://bugs.eclipse.org/bugs/";

	@GET("rest/bug/{bugId}")
	Mono<JSONBugResponse> getBugById(@Path("bugId") long bugId);

	@Headers("Cache-Control: public, max-age=86400, s-maxage=86400") // 60 * 60 * 24 = 86400s - cache for one day
	@GET("rest/bug")
	Mono<JSONBugResponse> getBugs(@Query("assigned_to") String assignee, @Query("product") List<String> product,
			@Query("component") List<String> component, @Query("limit") long limit, @Query("status") String status,
			@Query("creation_time") Date creationTime, @Query("last_change_time") Date lastChangeTime);

	@GET("rest/bug/{bugId}/comment")
	Mono<ResponseBody> getComments(@Path("bugId") long bugId);

	@GET("rest/bug/{bugId}/attachment")
	Mono<ResponseBody> getAttachments(@Path("bugId") long bugId);

}
