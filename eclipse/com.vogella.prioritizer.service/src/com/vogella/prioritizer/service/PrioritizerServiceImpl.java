package com.vogella.prioritizer.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.vogella.prioritizer.core.model.Bug;
import com.vogella.prioritizer.core.model.RankedBug;
import com.vogella.prioritizer.core.service.PrioritizerService;

import okhttp3.OkHttpClient;
import reactor.core.publisher.Mono;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class PrioritizerServiceImpl implements PrioritizerService {

	private PrioritizerApi prioritizerApi;
	private String[] args;

	@Reference
	void args(IApplicationContext context) {
		args = (String[]) context.getArguments().get("application.args");
	}

	void unargs(IApplicationContext context) {
		args = null;
	}

	public class ServerSettings {
		@Parameter(names = "-serverUrl", description = "Specify a custom server url for the prioritizer")
		private String serverUrl = "http://217.172.12.199:9002/";

		public String getServerUrl() {
			return serverUrl;
		}

		public void setServerUrl(String serverUrl) {
			this.serverUrl = serverUrl;
		}
	}

	@Activate
	public void createPrioritizerApi() {
		ServerSettings serverSettings = new ServerSettings();
		JCommander.newBuilder().acceptUnknownOptions(true).addObject(serverSettings).build().parse(args);

		final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES)
				.connectTimeout(3, TimeUnit.MINUTES)
				.build();

		String serverUrl = serverSettings.getServerUrl();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(serverUrl).client(httpClient)
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
		return prioritizerApi.getKeyWordUrl(bugzillaRequest).map(kwur -> kwur.getUrl());
	}
}
