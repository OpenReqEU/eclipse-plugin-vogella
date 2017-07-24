package com.vogella.spring.datacrawler.communication;

import java.util.List;

import com.vogella.spring.datacrawler.communication.dto.BugDtoWrapper;
import com.vogella.spring.datacrawler.communication.dto.BugIdsDto;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BugzillaApi {

	String BASE_URL = "https://bugs.eclipse.org/bugs/";

	@GET("show_bug.cgi?ctype=xml")
	Observable<BugDtoWrapper> getBugsForBugIds(@Query("id") List<Integer> ids);

	/**
	 * Method to request only the last created open bug IDs from lastSynced to NOW.
	 * 
	 * @param from
	 *            the timestamp the bugs were last synced
	 * @return An Observable that emits the last changed open bug IDs
	 */
	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&columnlist=bug_id&order=changeddate%20DESC%2C&bug_status=__open__&chfield=%5BBug%20creation%5D&chfieldto=Now&ctype=rdf")
	Single<BugIdsDto> getBugIdsSince(@Query("chfieldfrom") String from);

	// the following method is only used to request an equal amount of bugs for
	// each
	// priority to train a classifier
	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&columnlist=bug_id&limit=700&order=changeddate%20DESC%2C&bug_status=__closed__&ctype=rdf")
	Single<BugIdsDto> getBugIdsForPriority(@Query("priority") String prio1, @Query("priority") String prio2);

	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&columnlist=bug_id&limit=100&order=changeddate%20DESC%2C&bug_status=__open__&ctype=rdf")
	Single<BugIdsDto> getBugs();
}