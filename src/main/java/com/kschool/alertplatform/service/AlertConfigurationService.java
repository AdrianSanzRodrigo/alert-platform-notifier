package com.kschool.alertplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertConfigurationService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendNewConfig(String message) {
        kafkaTemplate.send("alerts-config", message);
    }
}
