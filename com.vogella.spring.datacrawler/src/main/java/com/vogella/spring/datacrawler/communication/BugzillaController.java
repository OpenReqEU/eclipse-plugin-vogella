package com.vogella.spring.datacrawler.communication;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vogella.spring.data.entities.Bug;
import com.vogella.spring.data.repositories.BugRepository;
import com.vogella.spring.datacrawler.ArffFileExporter;
import com.vogella.spring.datacrawler.communication.dto.BugDtoWrapper;
import com.vogella.spring.datacrawler.communication.dto.BugIdDto;
import com.vogella.spring.datacrawler.communication.dto.BugIdDtoWrapper;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
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

	@Autowired
	public BugzillaController(BugRepository bugRepository, ArffFileExporter fileexporter) {
		this.bugRepository = bugRepository;
		this.fileexporter = fileexporter;
		initBugzillaApi();
	}

	private void initBugzillaApi() {
		HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
		// httpLoggingInterceptor.setLevel(Level.BODY);

		OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor)
				.connectTimeout(3, TimeUnit.SECONDS).readTimeout(1, TimeUnit.MINUTES).build();

		Retrofit retrofitClient = new Retrofit.Builder().baseUrl(BugzillaApi.BASE_URL)
				.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
				.addConverterFactory(SimpleXmlConverterFactory.create()).client(okHttpClient).build();

		api = retrofitClient.create(BugzillaApi.class);
	}

	public void loadBugs() {
		compositeDisposable.add(Observable.concat(getBugIdsObservables()).subscribeOn(Schedulers.io())
				.flatMap((wrapper) -> Observable.just(wrapper.getBugIdDtos()))
				.subscribeWith(new DisposableObserver<List<BugIdDto>>() {

					List<Integer> idList = new ArrayList<>();

					@Override
					public void onNext(List<BugIdDto> result) {
						// List<Integer> idList = new ArrayList<>();
						result.forEach(bugIdTo -> idList.add(bugIdTo.getId()));
						// idListWrapper.add(idList);
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onComplete() {
						loadBugsForBugIds(splitList(idList));
					}
				}));
	}

	/**
	 * Bugzilla throws an Exception if the URL to request the bug details is too
	 * large. Therefore, this method takes a list of lists of bug ids and performs
	 * separate request for each of the lists.
	 * 
	 * @param bugIds
	 *            the list of lists of bug ids
	 */
	private void loadBugsForBugIds(List<List<Integer>> bugIds) {
		compositeDisposable.add(Observable.concat(getBugDetailsObservables(bugIds)).subscribeOn(Schedulers.io())
				.subscribeWith(new DisposableObserver<BugDtoWrapper>() {
					int loadedBugs = 0;

					@Override
					public void onNext(BugDtoWrapper result) {
						loadedBugs += result.getBugDtos().size();
						logger.log(Level.INFO, "Loaded bugs:" + loadedBugs);

						ArrayList<Bug> bugs = new ArrayList<>();
						result.getBugDtos().forEach(bugDto -> bugs.add(bugDto.getBugFromBugDto()));
						bugRepository.save(bugs);
					}

					@Override
					public void onError(Throwable e) {
						e.printStackTrace();
					}

					@Override
					public void onComplete() {
						fileexporter.exportBugData();
					}
				}));
	}

	/**
	 * Takes a list of lists of bug IDs. Returns a list of Observables. One
	 * Observable for each list. The Observables emit the bug details for the bug
	 * IDs.
	 * 
	 * @param ids
	 *            List with Lists of bug ids
	 * @return list of observables
	 */
	private List<Observable<BugDtoWrapper>> getBugDetailsObservables(List<List<Integer>> ids) {
		List<Observable<BugDtoWrapper>> observableList = new ArrayList<>();
		for (List<Integer> bugIds : ids) {
			observableList.add(api.getBugsForBugIds(bugIds));
		}
		return observableList;
	}

	/**
	 * Return a list of Observables to request an equal amount of bugs for each //
	 * priority to train a classifier.
	 * 
	 * @return list of Observables
	 */
	private List<Observable<BugIdDtoWrapper>> getBugIdsObservables() {
		List<Observable<BugIdDtoWrapper>> observableList = new ArrayList<>();
		observableList.add(api.getHighPriorityBugIds());
		observableList.add(api.getMediumPriorityBugIds());
		observableList.add(api.getLowPriorityBugIds());
		return observableList;
	}

	@PreDestroy
	public void dispose() {
		compositeDisposable.dispose();
	}

	/**
	 * Splits a list of bug ids in sublist of equal parts.
	 * 
	 * @param bugIds
	 *            the List of bug IDs to split
	 * @return list that contains the splitted lists
	 */
	private List<List<Integer>> splitList(List<Integer> bugIds) {
		List<List<Integer>> splittedLists = new ArrayList<List<Integer>>();
		int subListLength = 500;
		for (int i = 0; i < bugIds.size(); i += subListLength) {
			splittedLists.add(bugIds.subList(i, Math.min(bugIds.size(), i + subListLength)));
		}
		logger.log(Level.INFO, "Splitted list in " + splittedLists.size() + " parts");
		return splittedLists;
	}
}