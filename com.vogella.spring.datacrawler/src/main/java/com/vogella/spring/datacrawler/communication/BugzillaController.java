package com.vogella.spring.datacrawler.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vogella.spring.data.entities.Bug;
import com.vogella.spring.data.repositories.BugRepository;
import com.vogella.spring.datacrawler.communication.dto.BugIdsDto;
import com.vogella.spring.datacrawler.communication.dto.BugsDto;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Component
public class BugzillaController {

	private CompositeDisposable compositeDisposable = new CompositeDisposable();
	private BugzillaApi api;
	private BugRepository bugRepository;

	@Autowired
	public BugzillaController(BugRepository bugRepository) {
		this.bugRepository = bugRepository;
		initBugzillaApi();
	}

	private void initBugzillaApi() {
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
//		 httpLoggingInterceptor.setLevel(Level.BODY);

		OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
				.connectTimeout(3, TimeUnit.SECONDS).readTimeout(1, TimeUnit.MINUTES).build();

		Retrofit retrofitClient = new Retrofit.Builder().baseUrl(BugzillaApi.BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(SimpleXmlConverterFactory.create()).client(okHttpClient).build();

		api = retrofitClient.create(BugzillaApi.class);
	}

	public void loadBugs() {
		compositeDisposable.add(api.getBugIds().subscribeWith(new DisposableSingleObserver<BugIdsDto>() {

			@Override
			public void onSuccess(BugIdsDto t) {
				List<String> bugIds = new ArrayList<>();
				t.getBugIdDtos().forEach(bugIdDto -> bugIds.add(bugIdDto.getId()));
				loadBugsForBugIds(bugIds);
			}

			@Override
			public void onError(Throwable e) {
				e.printStackTrace();
			}
		}));
	}

	private void loadBugsForBugIds(List<String> list) {
		compositeDisposable.add(api.getBugsForBugIds(list).subscribeWith(new DisposableSingleObserver<BugsDto>() {

			@Override
			public void onSuccess(BugsDto t) {
				List<Bug> bugs = new ArrayList<>();
				t.getBugDtos().forEach(bugDto -> bugs.add(bugDto.getBugFromBugDto()));
				List<Bug> savedBugs = (List<Bug>) bugRepository.save(bugs);
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
