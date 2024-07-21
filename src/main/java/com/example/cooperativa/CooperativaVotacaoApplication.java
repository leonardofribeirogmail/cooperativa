package com.example.cooperativa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CooperativaVotacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CooperativaVotacaoApplication.class, args);
	}
}
