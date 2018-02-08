package com.vogella.prioritizer.service;

import org.osgi.service.component.annotations.Component;

import com.vogella.prioritizer.core.service.PrioritizerService;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class PrioritizerServiceImpl implements PrioritizerService {

	private PrioritizerApi prioritizerApi;

	public PrioritizerServiceImpl() {
		Retrofit retrofit = new Retrofit.Builder().baseUrl("http://localhost:8080")
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
		prioritizerApi = retrofit.create(PrioritizerApi.class);
	}

	@Override
	public Single<byte[]> getKeyWordImage(String assignee, int limit) {
		return prioritizerApi.getKeywordImageBytes(assignee, limit).map(response -> {
			return response.bytes();
		});
	}

}
