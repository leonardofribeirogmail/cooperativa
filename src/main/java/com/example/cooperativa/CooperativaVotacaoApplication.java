package com.example.cooperativa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication(exclude = {RedisRepositoriesAutoConfiguration.class})
public class CooperativaVotacaoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CooperativaVotacaoApplication.class, args);
	}
}
