package com.vogella.spring.datacrawler.issueextractor;

import java.util.List;

import com.vogella.spring.datacrawler.issueextractor.dto.BugDtoWrapper;
import com.vogella.spring.datacrawler.issueextractor.dto.BugIdsDto;

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
	@GET("buglist.cgi?classification=Eclipse&product=Platform&limit=520&emailassigned_to1=1&emailtype1=notsubstring&columnlist=bug_id&chfield=%5BBug%20creation%5D&chfieldfrom=2015-07-01&chfieldto=Now&order=changeddate%20DESC%2C&bug_status=CLOSED&bug_status=RESOLVED&ctype=rdf")
	Observable<BugIdsDto> getBugIdsForUserExclude(@Query("email1") String email);
	
	@GET("buglist.cgi?classification=Eclipse&product=Platform&limit=520&emailassigned_to1=1&emailtype1=substring&columnlist=bug_id&chfield=%5BBug%20creation%5D&chfieldfrom=2015-07-01&chfieldto=Now&order=changeddate%20DESC%2C&bug_status=CLOSED&bug_status=RESOLVED&ctype=rdf")
	Observable<BugIdsDto> getBugIdsForUser(@Query("email1") String email);
	
	@GET("buglist.cgi?classification=Eclipse&product=Platform&columnlist=bug_id&chfield=%5BBug%20creation%5D&chfieldfrom=2015-07-01&chfieldto=Now&order=changeddate%20DESC%2C&bug_status=CLOSED&bug_status=RESOLVED&ctype=rdf")
	Observable<BugIdsDto> getAllBugs();

	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&columnlist=bug_id&limit=20&order=changeddate%20DESC%2C&bug_status=__open__&ctype=rdf")
	Single<BugIdsDto> getBugs();
}