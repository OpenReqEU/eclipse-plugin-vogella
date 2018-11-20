package com.vogella.common.core;

import reactor.core.publisher.Mono;

public interface PeristanceService {
	Mono<Void> save(Object object);
}
