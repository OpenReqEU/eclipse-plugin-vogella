package com.vogella.prioritizer.service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.PriorityBug;
import com.vogella.prioritizer.core.service.PrioritizerService;

import okhttp3.OkHttpClient;
import reactor.core.publisher.Mono;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class PrioritizerServiceImpl implements PrioritizerService {

	private PrioritizerApi prioritizerApi;

	public PrioritizerServiceImpl() {

		final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES)
				.connectTimeout(3, TimeUnit.MINUTES).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl("http://localhost:9801/rest/").client(httpClient)
				.addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(ReactorCallAdapterFactory.create()).build();
		prioritizerApi = retrofit.create(PrioritizerApi.class);
	}

	@Override
	public Mono<byte[]> getKeyWordImage(String assignee, int width, int height, String product, String component,
			int limit) {
		return prioritizerApi.getKeywordImageBytes(assignee, width, height, product, component, limit).map(response -> {
			try {
				return response.bytes();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public Mono<List<PriorityBug>> getSuitableBugs(String assignee, String product, String component, int limit) {
		return prioritizerApi.getSuitableBugs(assignee, product, component, limit);
	}

	@Override
	public Mono<List<Bug>> getMostDiscussedBugsOfTheMonth(List<String> product, List<String> component) {
		return prioritizerApi.getMostDiscussedBugsOfTheMonth(product, component);
	}
}
