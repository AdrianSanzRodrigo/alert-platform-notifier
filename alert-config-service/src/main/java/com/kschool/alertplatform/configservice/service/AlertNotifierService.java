package com.kschool.alertplatform.configservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class AlertNotifierService {

    @Value(value = "${email.receiver}")
    private String emailReceiver;

    @Value(value = "${email.subject}")
    private String emailSubject;

    @Autowired
    public JavaMailSender emailSender;


    @KafkaListener(topics = "${alerts.topic.name}", groupId = "alert-platform")
    public void sendEmail(String msg) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailReceiver);
        message.setSubject(emailSubject);
        message.setText(msg);
        try {
            emailSender.send(message);
        }
        catch (Exception e) {
            System.console().printf("Error while sending");
        }
    }
}
