package com.vogella.spring.datacrawler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.vogella.spring.datacrawler.communication.BugzillaController;
import com.vogella.spring.datacrawler.data.repositories.BugRepository;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner jpaSample(BugRepository bugRepo, BugzillaController bugController) {
		return (args) -> {
			bugController.loadBugsForTrainingSet();
		};
	}
}
