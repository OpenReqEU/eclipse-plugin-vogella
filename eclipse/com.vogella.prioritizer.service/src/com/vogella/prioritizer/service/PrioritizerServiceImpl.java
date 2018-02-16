package com.vogella.prioritizer.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.service.PrioritizerService;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Component
public class PrioritizerServiceImpl implements PrioritizerService {

	private PrioritizerApi prioritizerApi;

	public PrioritizerServiceImpl() {

		final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS)
				.connectTimeout(60, TimeUnit.SECONDS).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl("http://localhost:8080").client(httpClient)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
		prioritizerApi = retrofit.create(PrioritizerApi.class);
	}

	@Override
	public Single<byte[]> getKeyWordImage(String assignee, int width, int height, String product, String component,
			int limit) {
		return prioritizerApi.getKeywordImageBytes(assignee, width, height, product, component, limit).map(response -> {
			return response.bytes();
		});
	}

	@Override
	public Single<List<Bug>> getSuitableBugs(String assignee, String product, String component, int limit) {
		return prioritizerApi.getSuitableBugs(assignee, product, component, limit);
	}

}
