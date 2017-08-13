package com.vogella.prioritizer.eclipseplugin.communication;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import com.vogella.spring.data.entities.Model;
import com.vogella.spring.data.entities.RankedBug;
import com.vogella.spring.data.entities.UserAccount;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class CommunicationController {

	private PrioritizerApi api;
	private CompositeDisposable disposable = new CompositeDisposable();

	private static CommunicationController instance;

	public static CommunicationController getInstance() {
		if (instance == null) {
			instance = new CommunicationController();
		}
		return instance;
	}

	private CommunicationController() {
		initApi();
	}

	private void initApi() {

		// TODO: remove timeout (for model generation)
		OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
				.readTimeout(30, TimeUnit.SECONDS).build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(PrioritizerApi.BASE_URL).client(okHttpClient)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create()).build();

		api = retrofit.create(PrioritizerApi.class);
	}

	public void postUserAccount(UserAccount userAccount, DisposableSingleObserver<Model> observer) {
		checkDisposable();
		disposable.add(api.postAccount(userAccount).subscribeOn(Schedulers.io()).subscribeWith(observer));
	}

	public void getIssues(DisposableSingleObserver<List<RankedBug>> observer) {
		checkDisposable();
		disposable.add(api.getBugs().subscribeOn(Schedulers.io()).subscribeWith(observer));
	}

	private void checkDisposable() {
		if (disposable.isDisposed()) {
			disposable = new CompositeDisposable();
		}
	}

	@PreDestroy
	private void onDestroy() {
		disposable.dispose();
	}
}
