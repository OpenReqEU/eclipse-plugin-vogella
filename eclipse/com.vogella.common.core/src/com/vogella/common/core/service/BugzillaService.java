package com.vogella.common.core.service;

import java.util.List;

import reactor.core.publisher.Mono;

public interface BugzillaService {
	/**
	 * Get the products of Eclipse Bugzilla.
	 * 
	 * @return {@link Mono} with a list of product names. Remember to sync with the
	 *         ui thread.
	 */
	Mono<List<String>> getProducts();

	/**
	 * Get the components of Eclipse Bugzilla.
	 * 
	 * @return {@link Mono} with a list of component names. Remember to sync with
	 *         the ui thread.
	 */
	Mono<List<String>> getComponents();
}
