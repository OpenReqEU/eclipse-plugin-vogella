package com.vogella.common.core.service;

import java.util.List;

import com.vogella.common.core.domain.Product;

import reactor.core.publisher.Mono;

public interface BugzillaServiceService {
	Mono<List<Product>> getProducts();
}
