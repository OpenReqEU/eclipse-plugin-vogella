package com.vogella.products.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.vogella.common.core.domain.Product;
import com.vogella.common.core.service.BugzillaServiceService;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import reactor.core.publisher.Mono;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class BugzillaServiceImpl implements BugzillaServiceService {

	private BugzillaApi bugzillaApi;

	@Activate
	public void activate() {
		final OkHttpClient httpClient = new OkHttpClient.Builder().readTimeout(3, TimeUnit.MINUTES)
				.connectTimeout(2, TimeUnit.MINUTES).addInterceptor(new HttpLoggingInterceptor().setLevel(Level.BODY))
				.build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl("http://openreq.ist.tugraz.at:9001").client(httpClient)
				.addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(ReactorCallAdapterFactory.create()).build();
		bugzillaApi = retrofit.create(BugzillaApi.class);
	}
	
	@Override
	public Mono<List<Product>> getProducts() {
		return bugzillaApi.getProducts();
	}

}
