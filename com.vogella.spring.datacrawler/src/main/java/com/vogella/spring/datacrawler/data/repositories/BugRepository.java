package com.vogella.spring.datacrawler.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.vogella.spring.datacrawler.data.entities.Bug;

public interface BugRepository extends CrudRepository<Bug, String> {

	@Query(value = "SELECT DISTINCT component FROM Bug")
	List<String> findAllDistinctComponents();

	@Query(value = "SELECT DISTINCT reporter FROM Bug")
	List<String> findAllDistinctReporters();

	@Query(value = "SELECT DISTINCT version FROM Bug")
	List<String> findAllDistinctVersions();

	@Query(value = "SELECT DISTINCT reportedPlatform FROM Bug")
	List<String> findAllDistinctReportedPlatforms();

	@Query(value = "SELECT DISTINCT operationSystem FROM Bug")
	List<String> findAllDistinctOperationSystems();

	@Query(value = "SELECT DISTINCT milestone FROM Bug")
	List<String> findAllDistinctMilestones();

	@Query(value = "SELECT DISTINCT priority FROM Bug")
	List<String> findAllDistinctPriorities();

	@Query(value = "SELECT DISTINCT severity FROM Bug")
	List<String> findAllDistinctSeverities();

	@Query(value = "SELECT DISTINCT assignedTo FROM Bug")
	List<String> findAllDistinctAssignedTo();
}
