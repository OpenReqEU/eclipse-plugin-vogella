package com.vogella.prioritizer.core.service;

import java.util.List;

import com.vogella.prioritizer.core.service.model.Bug;

import io.reactivex.Single;

public interface PrioritizerService {
	public Single<byte[]> getKeyWordImage(String assignee, int limit);

	public Single<List<Bug>> getSuitableBugs(String assignee, int limit);
}
