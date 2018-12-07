package com.vogella.rest.services;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;

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

	public InnoSensrServiceImpl() {
		final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES)
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
