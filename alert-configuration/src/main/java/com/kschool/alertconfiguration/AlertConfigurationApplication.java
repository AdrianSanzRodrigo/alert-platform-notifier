package com.kschool.alertconfiguration;

import com.kschool.alertplatform.security.annotations.EnableJwtAuthentication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@EnableJwtAuthentication
@SpringBootApplication
public class AlertConfigurationApplication {
	public static void main(String[] args) {
		SpringApplication.run(AlertConfigurationApplication.class, args);
	}
}
