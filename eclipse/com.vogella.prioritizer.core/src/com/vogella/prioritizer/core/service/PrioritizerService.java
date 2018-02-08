package com.vogella.prioritizer.core.service;

import io.reactivex.Single;

public interface PrioritizerService {
	public Single<byte[]> getKeyWordImage(String assignee, int limit);
}
