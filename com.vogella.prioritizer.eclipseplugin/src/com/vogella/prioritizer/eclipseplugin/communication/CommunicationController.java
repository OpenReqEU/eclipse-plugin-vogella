package com.vogella.prioritizer.eclipseplugin.communication;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;

import com.vogella.prioritizer.eclipseplugin.ui.parts.IUpdateView;
import com.vogella.spring.data.entities.RankedBug;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Creatable
@Singleton
public class CommunicationController {

	private PrioritizerApi api;
	private CompositeDisposable disposable = new CompositeDisposable();

	public CommunicationController() {
		initApi();
	}

	private void initApi() {
		OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();

		Retrofit retrofit = new Retrofit.Builder().baseUrl(PrioritizerApi.BASE_URL).client(okHttpClient)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(GsonConverterFactory.create()).build();

		api = retrofit.create(PrioritizerApi.class);
	}

	public void requestUiUpdate(IUpdateView updateView) {
		disposable.add(api.getBugs().subscribeOn(Schedulers.io())
				.subscribeWith(new DisposableSingleObserver<List<RankedBug>>() {

					@Override
					public void onError(Throwable arg0) {
						updateView.setError();
					}

					@Override
					public void onSuccess(List<RankedBug> bugs) {
						updateView.updateView(bugs);

					}
				}));
	}

	@PreDestroy
	private void onDestroy() {
		disposable.dispose();
	}
}
