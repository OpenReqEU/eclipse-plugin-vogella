package com.vogella.products.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory;
import com.vogella.common.core.domain.BugProduct;
import com.vogella.common.core.service.BugzillaServiceService;

import reactor.core.publisher.Mono;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Component
public class BugzillaServiceImpl implements BugzillaServiceService {

	private BugzillaApi bugzillaApi;
	private Mono<List<BugProduct>> cachedProducts;

	@Activate
	public void activate() {
		Retrofit retrofit = new Retrofit.Builder().baseUrl(BugzillaApi.BASE_URL)
				.addConverterFactory(JacksonConverterFactory.create())
				.addCallAdapterFactory(ReactorCallAdapterFactory.create()).build();
		bugzillaApi = retrofit.create(BugzillaApi.class);

		// call the products once the service is activated
//		cachedProducts = bugzillaApi.getProducts().subscribeOn(Schedulers.elastic());
		
		ArrayList<BugProduct> arrayList = new ArrayList<>();
		BugProduct product = new BugProduct();
		product.setName("Platform");
		product.setComponents(Collections.singletonList(new com.vogella.common.core.domain.BugComponent("UI")));
		arrayList.add(product);

		product = new BugProduct();
		product.setName("PDE");
		
		ArrayList<com.vogella.common.core.domain.BugComponent> arrayList2 = new ArrayList<>();
		arrayList2.add(new com.vogella.common.core.domain.BugComponent("CORE"));
		arrayList2.add(new com.vogella.common.core.domain.BugComponent("UI"));
		product.setComponents(arrayList2);
		arrayList.add(product);

		product = new BugProduct();
		product.setName("JDT");
		product.setComponents(Collections.singletonList(new com.vogella.common.core.domain.BugComponent("UI")));
		arrayList.add(product);
		
		
		cachedProducts = Mono.just(arrayList);
		cachedProducts.subscribe(pl -> pl.forEach(p -> System.out.println(p.getName())), Throwable::printStackTrace);
	}

	@Override
	public Mono<List<BugProduct>> getProducts() {
		return cachedProducts;
	}
}
