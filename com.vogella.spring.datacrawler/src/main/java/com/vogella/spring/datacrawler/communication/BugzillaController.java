package com.vogella.spring.datacrawler.communication;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.simpleframework.xml.convert.AnnotationStrategy;
import org.simpleframework.xml.core.Persister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vogella.spring.datacrawler.KeyValueStore;
import com.vogella.spring.datacrawler.communication.dto.BugDtoWrapper;
import com.vogella.spring.datacrawler.communication.dto.BugIdsDto;
import com.vogella.spring.datacrawler.data.DtoToJpaConverter;
import com.vogella.spring.datacrawler.data.entities.Bug;
import com.vogella.spring.datacrawler.data.repositories.BugRepository;
import com.vogella.spring.datacrawler.fileexporter.ArffFileExporter;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@Component
public class BugzillaController {

	private static final Logger logger = Logger.getLogger(BugzillaController.class.getName());

	private CompositeDisposable compositeDisposable = new CompositeDisposable();
	private BugzillaApi api;
	private BugRepository bugRepository;
	private ArffFileExporter fileexporter;
	private KeyValueStore keyValueStore;

	@Autowired
	public BugzillaController(BugRepository bugRepository, ArffFileExporter fileexporter,
			KeyValueStore datacrawlerPreferences) {
		this.bugRepository = bugRepository;
		this.fileexporter = fileexporter;
		this.keyValueStore = datacrawlerPreferences;
		initBugzillaApi();
	}

	private void initBugzillaApi() {
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
		// httpLoggingInterceptor.setLevel(okhttp3.logging.HttpLoggingInterceptor.Level.BODY);

		OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
				.connectTimeout(3, TimeUnit.SECONDS).readTimeout(1, TimeUnit.MINUTES).build();


		Retrofit retrofitClient = new Retrofit.Builder().baseUrl(BugzillaApi.BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(SimpleXmlConverterFactory.create(new Persister(new AnnotationStrategy())))
				.client(okHttpClient).build();

		api = retrofitClient.create(BugzillaApi.class);
	}

	/**
	 * Loads bugs for the training set.
	 */
	public void loadBugsForTrainingSet() {
		// Observable<BugIdsDto> observable =
		// Observable.concat(api.getBugIdsForPriority("P1", "P2"),
		// api.getBugIdsForPriority("P3", null), api.getBugIdsForPriority("P4", "P5"));
		loadBugs(api.getBugs());
	}

	/**
	 * Loads latest created bugs.
	 */
	public void loadLatestCreatedBugs() {
		String lastSynced = keyValueStore.getValue(KeyValueStore.LAST_SYNC_BUGS_KEY);
		Single<BugIdsDto> single = api
				.getBugIdsSince(lastSynced == null ? getFormattedTimestamp(System.currentTimeMillis()) : lastSynced);
		loadBugs(single);
	}

	/**
	 * Loads an initial set of bugs.
	 */
	public void loadInitialBugs() {
		long timestamp = System.currentTimeMillis() + (30 * 24 * 60 * 60 * 1000);
		Single<BugIdsDto> single = api.getBugIdsSince(getFormattedTimestamp(timestamp));
		loadBugs(single);
	}

	/**
	 * Method to request bugs.
	 * 
	 * This method first request the bug IDs and, if successful, requests the bug
	 * details for each of the IDs.
	 * 
	 * @param single
	 *            an Observable to request the bug IDs
	 */
	private void loadBugs(Single<BugIdsDto> single) {
		compositeDisposable
				.add(single.subscribeOn(Schedulers.io()).flatMap((wrapper) -> Single.just(wrapper.getBugIds()))
						.subscribeWith(new DisposableSingleObserver<List<Integer>>() {

							@Override
							public void onSuccess(List<Integer> t) {
								logger.log(Level.INFO, "Loaded bug ids:" + t.size());
								loadBugDetailsForBugIds(t);
							}

							@Override
							public void onError(Throwable e) {
								e.printStackTrace();
							}
						}));
	}

	/**
	 * Requests the bug details for a list of bug IDs.
	 * 
	 * @param bugIds
	 *            the list of bug IDs
	 */
	private void loadBugDetailsForBugIds(List<Integer> bugIds) {
		compositeDisposable.add(getBugDetailsObservable(bugIds).subscribeOn(Schedulers.io())
				.subscribeWith(new DisposableObserver<BugDtoWrapper>() {
					int loadedBugs = 0;

					@Override
					public void onNext(BugDtoWrapper result) {
						loadedBugs += result.getBugDtos().size();
						logger.log(Level.INFO, "Loaded bug details:" + loadedBugs);

						ArrayList<Bug> bugs = new ArrayList<>();
						result.getBugDtos().forEach(bugDto -> bugs.add(DtoToJpaConverter.getBugFromBugDto(bugDto)));
						bugRepository.save(bugs);
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onComplete() {
						keyValueStore.setValue(KeyValueStore.LAST_SYNC_BUGS_KEY,
								getFormattedTimestamp(System.currentTimeMillis()));
						// fileexporter.exportBugData();
					}
				}));
	}

	/**
	 * Takes a list of bug IDs. Returns an Observable. The Observable emits the bug
	 * details for the bug IDs.
	 * 
	 * @param ids
	 *            list of bug ids
	 * @return an Observable
	 */
	private Observable<BugDtoWrapper> getBugDetailsObservable(List<Integer> ids) {
		List<Observable<BugDtoWrapper>> observableList = new ArrayList<>();
		for (List<Integer> bugIds : splitList(ids)) {
			observableList.add(api.getBugsForBugIds(bugIds));
		}
		return Observable.concat(observableList);
	}

	/**
	 * Splits a list of bug IDs in sublist.
	 * 
	 * @param bugIds
	 *            the list of bug IDs to split
	 * @return list that contains the splitted lists
	 */
	private List<List<Integer>> splitList(List<Integer> bugIds) {
		List<List<Integer>> splittedLists = new ArrayList<List<Integer>>();
		int subListLength = 25;
		for (int i = 0; i < bugIds.size(); i += subListLength) {
			splittedLists.add(bugIds.subList(i, Math.min(bugIds.size(), i + subListLength)));
		}
		logger.log(Level.INFO, "Splitted list in " + splittedLists.size() + " parts");
		return splittedLists;
	}

	private String getFormattedTimestamp(long timemillis) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
		Date date = new Date(timemillis);
		return dateFormat.format(date);
	}

	@PreDestroy
	public void dispose() {
		compositeDisposable.dispose();
	}
}