package com.vogella.prioritizer.eclipseplugin.communication;

import java.util.List;

import com.vogella.spring.data.entities.RankedBug;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface PrioritizerApi {

	String BASE_URL = "http://localhost:8080";

	@GET("bugs")
	Observable<List<RankedBug>> getBugs();
}
