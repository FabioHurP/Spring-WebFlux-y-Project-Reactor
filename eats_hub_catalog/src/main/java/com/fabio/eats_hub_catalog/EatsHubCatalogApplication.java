package com.fabio.eats_hub_catalog;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// @SpringBootApplication
// public class EatsHubCatalogApplication {

// 	public static void main(String[] args) {
// 		SpringApplication.run(EatsHubCatalogApplication.class, args);
// 	}

@SpringBootApplication
public class EatsHubCatalogApplication implements CommandLineRunner {

	@Autowired
	private Repo repo;
	public static void main(String[] args) {
		SpringApplication.run(EatsHubCatalogApplication.class, args);
	}

	@Override 
	public void run(String... args) throws Exception {
		this.repo.findAll()
			.doOnNext(System.out::println)
			.subscribe();
	}


}
