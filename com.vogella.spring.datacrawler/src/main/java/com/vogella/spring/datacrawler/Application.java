package com.vogella.spring.datacrawler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vogella.spring.data.repositories.BugRepository;
import com.vogella.spring.datacrawler.communication.BugzillaController;

@SpringBootApplication
@EnableScheduling
@EntityScan("com.vogella.spring.data.entities")
@EnableJpaRepositories("com.vogella.spring.data.repositories")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner jpaSample(BugRepository bugRepo, BugzillaController bugController) {
		return (args) -> {
			bugController.loadBugsForTrainingSet();

			// query for all bugs in the H2 database and print them
			// bugRepo.findAll().forEach(System.out::println);
		};
	}
}
