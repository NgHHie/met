package ptit.dblab.app;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "ptit.dblab")
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
@Slf4j
public class DblabApplication {
	static {
		try {
			Dotenv dotenv = Dotenv.load(); // Loads .env file into System Environment
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		} catch (Exception e) {
			log.warn("Failed to load system properties", e);
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(DblabApplication.class, args);
	}
}
