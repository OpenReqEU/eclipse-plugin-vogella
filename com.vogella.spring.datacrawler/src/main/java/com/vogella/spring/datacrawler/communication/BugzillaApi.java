package com.vogella.spring.datacrawler.communication;

import com.vogella.spring.datacrawler.communication.dto.BugListDto;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface BugzillaApi {
	
	@GET("buglist.cgi?classification=Eclipse&component=Ui&product=Platform&limit=10&columnlist=bug_id%2Cassigned_to%2Cshort_desc%2Cproduct%2Ccomponent&ctype=rdf")
	Single<BugListDto> loadBugs();
}