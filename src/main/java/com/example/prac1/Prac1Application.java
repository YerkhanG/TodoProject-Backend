package com.example.prac1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class Prac1Application {

	public static void main(String[] args) {
		SpringApplication.run(Prac1Application.class, args);
	}

}
