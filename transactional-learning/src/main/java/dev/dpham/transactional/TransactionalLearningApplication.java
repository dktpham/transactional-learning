package dev.dpham.transactional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.transaction.jta.JtaAutoConfiguration;
import org.springframework.retry.annotation.EnableRetry;

@EnableRetry
@SpringBootApplication(exclude = {JtaAutoConfiguration.class})
public class TransactionalLearningApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionalLearningApplication.class, args);
	}

}
