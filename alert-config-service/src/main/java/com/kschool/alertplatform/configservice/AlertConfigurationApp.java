package com.kschool.alertplatform.configservice;

import com.kschool.alertplatform.security.annotations.EnableJwtAuthentication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@EnableJwtAuthentication
@SpringBootApplication
public class AlertConfigurationApp {
	public static void main(String[] args) {
		SpringApplication.run(AlertConfigurationApp.class, args);
	}
}
