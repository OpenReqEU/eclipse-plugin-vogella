package com.vogella.common.core.service;

import com.vogella.common.core.domain.CommandStats;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CommandStatsPersistenceService {
	Mono<Void> add(CommandStats object);
	Mono<Void> remove(CommandStats object);
	Flux<CommandStats> get();
	Mono<Void> persist();
}
