package by.nikiforova.epic_auth_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class EpicAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EpicAuthServiceApplication.class, args);
	}

}
