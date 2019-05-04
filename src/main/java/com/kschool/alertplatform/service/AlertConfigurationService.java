package com.kschool.alertplatform.service;

import com.kschool.alertplatform.model.Alert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertConfigurationService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendAlertConfig(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }

    public List<Alert> getAlertsConfig(String userId) {

    }
}
