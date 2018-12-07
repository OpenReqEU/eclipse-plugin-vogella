package com.vogella.products.service;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vogella.common.core.service.BugzillaService;

import reactor.core.publisher.Mono;

@Component
public class BugzillaServiceImpl implements BugzillaService {

	private Mono<List<String>> cachedProducts;
	private Mono<List<String>> cachedComponents;

	@Activate
	public void activate(BundleContext bundleContext) {
		Mono<List<String>> productread = getJsonReadMono(bundleContext, "resources/products.json");
		cachedProducts = productread.retry(3).cache();
		cachedProducts.subscribe();

		Mono<List<String>> componentRead = getJsonReadMono(bundleContext, "resources/components.json");
		cachedComponents = componentRead.retry(3).cache();
		cachedComponents.subscribe();
	}

	private Mono<List<String>> getJsonReadMono(BundleContext bundleContext, String filePath) {
		Mono<List<String>> productread = Mono.create(sink -> {
			Job job = Job.create("Fetching " + filePath, monitor -> {
				Bundle bundle = bundleContext.getBundle();
				URL resource = bundle.getResource(filePath);
				try(InputStreamReader isr = new InputStreamReader(resource.openStream())) {
					Type listType = new TypeToken<ArrayList<String>>(){}.getType();

					Gson gson = new GsonBuilder().create();
					ArrayList<String> fromJson = gson.fromJson(isr, listType);
					
					sink.success(fromJson);
				} catch (Exception e) {
					sink.error(e);
				}
			});
			job.schedule();
		});
		return productread;
	}

	@Override
	public Mono<List<String>> getProducts() {
		return cachedProducts;
	}

	@Override
	public Mono<List<String>> getComponents() {
		return cachedComponents;
	}
}
