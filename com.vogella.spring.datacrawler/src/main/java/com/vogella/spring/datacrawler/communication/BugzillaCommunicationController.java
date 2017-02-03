package com.vogella.spring.datacrawler.communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.vogella.spring.datacrawler.data.entities.Bug;
import com.vogella.spring.datacrawler.repository.BugRepository;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

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
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create()).client(okHttpClient).build();

		api = retrofitClient.create(BugzillaApi.class);
	}

	@Scheduled(cron = "0 0 23 ? * MON-FRI")
	public void saveBugs() {
		compositeDisposable.add(api.loadBugs().subscribeWith(new DisposableSingleObserver<ResponseBody>() {

			@Override
			public void onSuccess(ResponseBody responseBody) {
				try {
					List<Bug> bugs = getBugs(responseBody);
					bugRepository.save(bugs);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError(Throwable e) {

			}
		}));
	}

	private List<Bug> getBugs(ResponseBody responseBody) throws IOException {
		List<Bug> bugs = new ArrayList<>();
		String line = "";
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseBody.byteStream()))) {
			while ((line = reader.readLine()) != null) {

				// use comma as separator
				String[] docLine = line.split(",");

				Bug bug = new Bug();
				bug.setId(docLine[0]);
				bug.setDescription(docLine[2]);
				bug.setAssignedTo(docLine[1]);
				bug.setProduct(docLine[3]);
				bug.setComponent(docLine[4]);

				bugs.add(bug);
			}
		}

		return bugs;
	}

	@PreDestroy
	public void dispose() {
		compositeDisposable.dispose();
	}
}
