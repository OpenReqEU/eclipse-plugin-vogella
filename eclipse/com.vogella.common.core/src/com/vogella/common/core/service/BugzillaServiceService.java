package com.vogella.common.core.service;

import java.util.List;

import com.vogella.common.core.domain.BugProduct;

import reactor.core.publisher.Mono;

public interface BugzillaServiceService {
	Mono<List<BugProduct>> getProducts();
}
