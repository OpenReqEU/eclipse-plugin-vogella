package com.vogella.prioritizer.core.service;

import java.util.List;

import com.vogella.prioritizer.core.model.PriorityBug;

import io.reactivex.Single;

public interface PrioritizerService {
	public Single<byte[]> getKeyWordImage(String assignee,int width, int height, String product, String component, int limit);

	public Single<List<PriorityBug>> getSuitableBugs(String assignee, String product, String component, int limit);
}
