package com.vogella.spring.datacrawler.communication;

import java.util.List;

import com.vogella.spring.datacrawler.communication.dto.BugsDto;
import com.vogella.spring.datacrawler.communication.dto.BugIdsDto;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BugzillaApi {

	String BASE_URL = "https://bugs.eclipse.org/bugs/";

	@GET("buglist.cgi?classification=Eclipse&component=Ui&product=Platform&columnlist=bug_id&limit=2&ctype=rdf")
	Single<BugIdsDto> getBugIds();

	@GET("show_bug.cgi?ctype=xml")
	Single<BugsDto> getBugsForBugIds(@Query("id") List<String> ids);
}