package com.vogella.prioritizer.eclipseplugin.communication;

import java.util.List;

import com.vogella.spring.data.entities.Model;
import com.vogella.spring.data.entities.RankedBug;
import com.vogella.spring.data.entities.UserAccount;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface PrioritizerApi {

	String BASE_URL = "http://localhost:8080/";

	@GET("bugs")
	Single<List<RankedBug>> getBugs();

	@POST("user")
	Single<Model> postAccount(@Body UserAccount userAccount);
}
