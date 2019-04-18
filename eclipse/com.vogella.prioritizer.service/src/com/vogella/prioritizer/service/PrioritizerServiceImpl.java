package com.vogella.prioritizer.service;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
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
	private IProxyService proxyService;
	private String[] args;

	public class ServerSettings {
		@Parameter(names = "-serverUrl", description = "Specify a custom server url for the prioritizer")
		private String serverUrl = "http://www.tigerpirates.com:10002";

		public String getServerUrl() {
			return serverUrl;
		}

		public void setServerUrl(String serverUrl) {
			this.serverUrl = serverUrl;
		}
	}

	@Reference
	void args(IApplicationContext context) {
		args = (String[]) context.getArguments().get("application.args");
	}

	void unargs(IApplicationContext context) {
		args = null;
	}

	@Reference
	public void bindProxyService(IProxyService proxyService) {
		this.proxyService = proxyService;
	}

	public void unbindProxyService(IProxyService proxyService) {
		this.proxyService = null;
	}

	@Activate
	public void activate() {
		ServerSettings serverSettings = new ServerSettings();
		JCommander.newBuilder().acceptUnknownOptions(true).addObject(serverSettings).build().parse(args);
		String serverUrl = serverSettings.getServerUrl();
		URI uri = URI.create(serverUrl);
		IProxyData[] proxyDataForHost = proxyService.select(uri);
		Proxy proxy = null;
		for (IProxyData data : proxyDataForHost) {
			if (data.getHost() != null) {
				System.setProperty("http.proxySet", "true");
				System.setProperty("http.proxyHost", data.getHost());
			}
			if (data.getHost() != null) {
				System.setProperty("http.proxyPort", String.valueOf(data.getPort()));
			}

			String type = data.getType();

			InetSocketAddress inetSocketAddress = new InetSocketAddress(data.getHost(), data.getPort());

			proxy = new Proxy(Type.valueOf(type), inetSocketAddress);
		}

		if (proxy == null) {
			proxy = Proxy.NO_PROXY;
		}

		final OkHttpClient httpClient = new OkHttpClient.Builder().proxy(proxy)
				.addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY)).readTimeout(3, TimeUnit.MINUTES)
				.connectTimeout(3, TimeUnit.MINUTES).build();

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
	public Mono<BugzillaPriorityResponse> unlikeBug(String agentID, long bugId, String assignee, List<String> product,
			List<String> component) {
		PrioritizerIdRequest idRequest = new PrioritizerIdRequest();
		idRequest.setAgent_id(agentID);
		idRequest.setId(bugId);
		idRequest.setAssignee(assignee);
		idRequest.setProducts(product);
		idRequest.setComponents(component);
		return prioritizerApi.unlikeBug(idRequest);
	}

	@Override
	public Mono<BugzillaPriorityResponse> deferBug(String agentID, long bugId, int interval, String assignee,
			List<String> product, List<String> component) {
		PrioritizerIdIntervalRequest idRequest = new PrioritizerIdIntervalRequest();
		idRequest.setAgent_id(agentID);
		idRequest.setId(bugId);
		idRequest.setInterval(interval);
		idRequest.setAssignee(assignee);
		idRequest.setProducts(product);
		idRequest.setComponents(component);
		return prioritizerApi.deferBug(idRequest);
	}

	@Override
	public Mono<BugzillaPriorityResponse> deleteProfile(String agentId) {
		return prioritizerApi.deleteProfile(new DeleteProfile(agentId));
	}
}
