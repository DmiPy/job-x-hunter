package dev.dmitriy.job_x_hunter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobXHunterApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobXHunterApplication.class, args);
	}

}
