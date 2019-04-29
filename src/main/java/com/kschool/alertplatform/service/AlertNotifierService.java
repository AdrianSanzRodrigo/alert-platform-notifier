package com.kschool.alertplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class AlertNotifierService {

    @KafkaListener(topics = "alerts", groupId = "alert-platform")
    public void sendEmail(String msg) {
        System.out.println("A new alert has been received: " + msg);
    }
}
