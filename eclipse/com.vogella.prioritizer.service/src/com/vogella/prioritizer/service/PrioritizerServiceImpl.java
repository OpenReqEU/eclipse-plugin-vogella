package com.vogella.prioritizer.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.RankedBug;
import com.vogella.prioritizer.core.service.PrioritizerService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import reactor.core.publisher.Mono;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class PrioritizerServiceImpl implements PrioritizerService {

	private PrioritizerApi prioritizerApi;

	public PrioritizerServiceImpl() {
		final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES)
				.connectTimeout(3, TimeUnit.MINUTES).addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
				.build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl("http://217.172.12.199:9002/").client(httpClient)
				.addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(ReactorCallAdapterFactory.create()).build();
		prioritizerApi = retrofit.create(PrioritizerApi.class);
	}

	@Override
	public Mono<List<RankedBug>> getSuitableBugs(String assignee, List<String> product, List<String> component) {
		BugzillaRequest bugzillaRequest = new BugzillaRequest();
		bugzillaRequest.setAssignee(assignee);
		bugzillaRequest.setProducts(product);
		bugzillaRequest.setComponents(component);
		Mono<BugzillaPriorityResponse> suitableBugs = prioritizerApi.getSuitableBugs(bugzillaRequest);
		return suitableBugs.map(BugzillaPriorityResponse::getRankedBugs);
	}

	@Override
	public Mono<List<Bug>> getMostDiscussedBugsOfTheMonth(List<String> product, List<String> component) {
		return prioritizerApi.getMostDiscussedBugsOfTheMonth(product, component);
	}

	@Override
	public Mono<String> getKeyWordUrl(String assignee, List<String> product, List<String> component) {
		BugzillaRequest bugzillaRequest = new BugzillaRequest();
		bugzillaRequest.setAssignee(assignee);
		bugzillaRequest.setProducts(product);
		bugzillaRequest.setComponents(component);
		// TODO replace is a current workaround for port problems
		return prioritizerApi.getKeyWordUrl(bugzillaRequest)
				.map(kwur -> kwur.getUrl().replace("openreq.ist.tugraz.at", "78.47.88.29"));
	}
}
