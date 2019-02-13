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
import com.vogella.prioritizer.core.model.BugzillaPriorityResponse;
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
		private String serverUrl = "http://openreq.ist.tugraz.at:9002";

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

		final OkHttpClient httpClient = new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
				.readTimeout(3, TimeUnit.MINUTES).connectTimeout(3, TimeUnit.MINUTES).build();

		String serverUrl = serverSettings.getServerUrl();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(serverUrl).client(httpClient)
				.addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(ReactorCallAdapterFactory.create()).build();
		prioritizerApi = retrofit.create(PrioritizerApi.class);
	}

	@Override
	public Mono<List<RankedBug>> getSuitableBugs(String agentID, String assignee, List<String> product,
			List<String> component) {
		PrioritizerRequest bugzillaRequest = new PrioritizerRequest();
		bugzillaRequest.setAgent_id(agentID);
		bugzillaRequest.setAssignee(assignee);
		bugzillaRequest.setProducts(product);
		bugzillaRequest.setComponents(component);
		Mono<BugzillaPriorityResponse> suitableBugs = prioritizerApi.getSuitableBugs(bugzillaRequest);
		return suitableBugs.map(BugzillaPriorityResponse::getRankedBugs);
	}

	@Override
	public Mono<List<Bug>> getMostDiscussedBugs(List<String> product, List<String> component, long daysBack) {
		return prioritizerApi.getMostDiscussedBugs(product, component, daysBack);
	}

	@Override
	public Mono<String> getKeyWordUrl(String agentID, String assignee, List<String> product, List<String> component) {
		PrioritizerRequest bugzillaRequest = new PrioritizerRequest();
		bugzillaRequest.setAgent_id(agentID);
		bugzillaRequest.setAssignee(assignee);
		bugzillaRequest.setProducts(product);
		bugzillaRequest.setComponents(component);
		// TODO replace is a current workaround for port problems
		return prioritizerApi.getKeyWordUrl(bugzillaRequest).map(kwur -> kwur.getUrl());
	}

	@Override
	public Mono<BugzillaPriorityResponse> dislikeBug(String agentID, long bugId, String assignee, List<String> product,
			List<String> component) {
		PrioritizerIdRequest idRequest = new PrioritizerIdRequest();
		idRequest.setAgent_id(agentID);
		idRequest.setId(bugId);
		idRequest.setAssignee(assignee);
		idRequest.setProducts(product);
		idRequest.setComponents(component);
		return prioritizerApi.dislikeBug(idRequest);
	}

	@Override
	public Mono<BugzillaPriorityResponse> likeBug(String agentID, long bugId, String assignee, List<String> product,
			List<String> component) {
		PrioritizerIdRequest idRequest = new PrioritizerIdRequest();
		idRequest.setAgent_id(agentID);
		idRequest.setId(bugId);
		idRequest.setAssignee(assignee);
		idRequest.setProducts(product);
		idRequest.setComponents(component);
		return prioritizerApi.likeBug(idRequest);
	}

	@Override
	public Mono<BugzillaPriorityResponse> deferBug(String agentID, long bugId, int interval, String assignee, List<String> product,
			List<String> component) {
		PrioritizerIdIntervalRequest idRequest = new PrioritizerIdIntervalRequest();
		idRequest.setAgent_id(agentID);
		idRequest.setId(bugId);
		idRequest.setInterval(interval);
		idRequest.setAssignee(assignee);
		idRequest.setProducts(product);
		idRequest.setComponents(component);
		return prioritizerApi.deferBug(idRequest);
	}
}
