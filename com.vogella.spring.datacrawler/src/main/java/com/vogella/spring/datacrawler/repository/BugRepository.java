package com.vogella.spring.datacrawler.repository;

import org.springframework.data.repository.CrudRepository;

import com.vogella.spring.datacrawler.data.entities.Bug;

public interface BugRepository extends CrudRepository<Bug, String> {

}
