package com.vogella.datacrawler.communication;

import java.util.concurrent.TimeUnit;

import com.vogella.datacrawler.data.handler.IDataHandler;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

public class BugzillaCommunicationController{

    static final String BASE_URL = "https://bugs.eclipse.org/bugs/";

    Retrofit retrofitClient;
    BugzillaApi api;
   
	public BugzillaCommunicationController() {
		OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS).readTimeout(1, TimeUnit.MINUTES).build();
		
		retrofitClient = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .build();
		
		
		api = retrofitClient.create(BugzillaApi.class);
	}
	
	public void getBugs(IDataHandler dataHandler) {
		api.loadBugs().enqueue(new BugzillaCallback(dataHandler));
	}	
}
