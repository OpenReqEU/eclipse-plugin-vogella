	package com.vogella.prioritizer.bugzilla.config;

import java.io.File;
import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.vogella.prioritizer.bugzilla.BugzillaApi;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Configuration
public class BugzillaBeans {

	private Retrofit retrofit;

	private static final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
	    @Override public Response intercept(Chain chain) throws IOException {
	        Response originalResponse = chain.proceed(chain.request());
	            int maxAge = 60 * 60 * 24; // read from cache for 1 day
	            return originalResponse.newBuilder()
	                    .header("Cache-Control", "public, max-age=" + maxAge + ", s-maxage=" + maxAge)
	                    .build();
	    }
	};
	
	@Bean
	public BugzillaApi getBugzillaApi() {
		if (null == retrofit) {
			Builder okHttpClientBuilder = new OkHttpClient.Builder();
			okHttpClientBuilder.networkInterceptors().add(REWRITE_CACHE_CONTROL_INTERCEPTOR);
			okHttpClientBuilder.cache(setUpHttpCache(okHttpClientBuilder));

			retrofit = new Retrofit.Builder().baseUrl(BugzillaApi.BASE_URL)
					.client(okHttpClientBuilder.build())
					.addCallAdapterFactory(ReactorCallAdapterFactory.create())
					.addConverterFactory(JacksonConverterFactory.create()).build();
		}

		return retrofit.create(BugzillaApi.class);
	}

	private Cache setUpHttpCache(Builder clientBuilder) {
		File httpCacheDirectory = new File(System.getProperty("user.home") + File.separator +
				"openreq-mostdiscussed-bugs" + File.separator + "okhttp-cache");
		httpCacheDirectory.mkdirs();
		int cacheSize = 50 * 1024 * 1024; // 50 MiB
		return new Cache(httpCacheDirectory, cacheSize);
	}
}
