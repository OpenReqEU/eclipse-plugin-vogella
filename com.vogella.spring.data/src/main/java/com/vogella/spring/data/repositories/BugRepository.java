package com.vogella.spring.data.repositories;

import org.springframework.data.repository.CrudRepository;

import com.vogella.spring.data.entities.Bug;

public interface BugRepository extends CrudRepository<Bug, String> {

}
