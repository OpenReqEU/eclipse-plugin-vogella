package com.vogella.spring.datacrawler.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vogella.spring.datacrawler.communication.dto.BugListDto;
import com.vogella.spring.datacrawler.data.entities.Bug;
import com.vogella.spring.datacrawler.repository.BugRepository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Component
public class BugzillaCommunicationController {

	private static final String BASE_URL = "https://bugs.eclipse.org/bugs/";

	private CompositeDisposable compositeDisposable = new CompositeDisposable();

	private Retrofit retrofitClient;
	private BugzillaApi api;

	private BugRepository bugRepository;

	@Autowired
	public BugzillaCommunicationController(BugRepository bugRepository) {
		this.bugRepository = bugRepository;
		OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS)
				.readTimeout(1, TimeUnit.MINUTES).build();

		retrofitClient = new Retrofit.Builder().baseUrl(BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(SimpleXmlConverterFactory.create()).client(okHttpClient).build();

		api = retrofitClient.create(BugzillaApi.class);
	}

	@Scheduled(cron = "0 0 23 ? * MON-FRI")
	public void saveBugs() {
		compositeDisposable.add(api.loadBugs().subscribeWith(new DisposableSingleObserver<BugListDto>() {

			@Override
			public void onSuccess(BugListDto responseBody) {
				List<Bug> bugListToSave = new ArrayList<>();
				responseBody.getBugList().forEach(bugDto -> bugListToSave.add(bugDto.getBugFromBugDto()));
				bugRepository.save(bugListToSave);
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
		}));
	}

	@PreDestroy
	public void dispose() {
		compositeDisposable.dispose();
	}
}
