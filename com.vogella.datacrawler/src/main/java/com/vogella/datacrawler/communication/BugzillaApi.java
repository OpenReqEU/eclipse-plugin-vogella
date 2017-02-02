package com.vogella.datacrawler.communication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface BugzillaApi {

	@GET("buglist.cgi?classification=Eclipse&component=Ui&product=Platform&limit=10&columnlist=bug_id%2Cassigned_to%2Cshort_desc%2Cproduct%2Ccomponent&ctype=csv")
	Call<ResponseBody> loadBugs();
}
