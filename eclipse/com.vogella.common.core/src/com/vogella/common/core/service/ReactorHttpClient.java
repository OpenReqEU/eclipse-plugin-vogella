package com.vogella.common.core.service;

import com.fasterxml.jackson.core.type.TypeReference;

import reactor.core.publisher.Mono;

public interface ReactorHttpClient {

	<T> Mono<T> get(String url, Class<T> responseType);
	
	<T> Mono<T> get(String url, TypeReference<T> responseType);

	<T> Mono<T> post(String url, Object body, Class<T> responseType);

	<T> Mono<T> put(String url, Object body, Class<T> responseType);

	<T> Mono<T> delete(String url, Object body, Class<T> responseType);
}