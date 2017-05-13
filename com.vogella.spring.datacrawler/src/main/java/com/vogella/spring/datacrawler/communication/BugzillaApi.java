package com.vogella.spring.datacrawler.communication;

import java.util.List;

import com.vogella.spring.datacrawler.communication.dto.BugDtoWrapper;
import com.vogella.spring.datacrawler.communication.dto.BugIdDtoWrapper;

import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BugzillaApi {

	String BASE_URL = "https://bugs.eclipse.org/bugs/";

	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&columnlist=bug_id&limit=1000&order=changeddate%20DESC%2C&bug_status=__closed__&ctype=rdf")
	Single<BugIdDtoWrapper> getBugIds();

	@GET("show_bug.cgi?ctype=xml")
	Observable<BugDtoWrapper> getBugsForBugIds(@Query("id") List<Integer> ids);

	// the following methods are only used to request an equal amount of bugs for
	// each
	// priority to train a classifier
	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&priority=P1&priority=P2&columnlist=bug_id&limit=700&order=changeddate%20DESC%2C&bug_status=__closed__&ctype=rdf")
	Observable<BugIdDtoWrapper> getHighPriorityBugIds();

	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&priority=P3&columnlist=bug_id&limit=700&order=changeddate%20DESC%2C&bug_status=__closed__&ctype=rdf")
	Observable<BugIdDtoWrapper> getMediumPriorityBugIds();

	@GET("buglist.cgi?classification=Eclipse&product=Platform&component=UI&priority=P4&priority=P5&columnlist=bug_id&limit=700&order=changeddate%20DESC%2C&bug_status=__closed__&ctype=rdf")
	Observable<BugIdDtoWrapper> getLowPriorityBugIds();
}