package com.vogella.rest.services;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.vogella.services.innosensr.InnoSensrService;
import com.vogella.services.innosensr.InnoSensrStatus;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import reactor.core.publisher.Mono;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class InnoSensrServiceImpl implements InnoSensrService {

	private InnoSensrApi innoSensrApi;
	private IProxyService proxyService;

	@Reference
	public void bindProxyService(IProxyService proxyService) {
		this.proxyService = proxyService;
	}

	public void unbindProxyService(IProxyService proxyService) {
		this.proxyService = null;
	}

	@Activate
	public void activate() {
		URI uri = URI.create("http://openreq.ist.tugraz.at:9001");
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

		final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES).proxy(proxy)
				.connectTimeout(2, TimeUnit.MINUTES).addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
				.build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl("http://openreq.ist.tugraz.at:9001").client(httpClient)
				.addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(ReactorCallAdapterFactory.create()).build();
		innoSensrApi = retrofit.create(InnoSensrApi.class);
	}

	@Override
	public Mono<Void> createProject(String title, String description) {
		return innoSensrApi.createProject(title, description);
	}

	@Override
	public Mono<Void> createRequirement(String projectUniqueKey, String title, String description,
			InnoSensrStatus status) {
		InnoSensrRequirement requirement = new InnoSensrRequirement(projectUniqueKey, title, description,
				status.toString());
		return innoSensrApi.createRequirement(requirement);
	}

}
