package com.kschool.alertplatform.controller;

import com.kschool.alertplatform.model.Alert;
import com.kschool.alertplatform.security.domain.User;
import com.kschool.alertplatform.service.AlertConfigurationService;
import com.kschool.alertplatform.service.AlertNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
public class AlertPlatformController {

    @Autowired
    private AlertConfigurationService alertConfigurationService;

    @Autowired
    private AlertNotifierService alertNotifierService;

    @Value(value = "${alerts.topic.name}")
    private String alertsTopicName;

    @Value(value = "${alerts-config.topic.name}")
    private String alertsConfigTopicName;

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Alert>> getAlertConfig(@AuthenticationPrincipal final User user) {
        final List<Alert> alertSettings = alertConfigurationService.getAlertsConfig(user.getClientId());
        return new ResponseEntity<>(alertSettings, HttpStatus.OK);
    }

    @PutMapping
    public void updateAlertConfig(){
        alertConfigurationService.sendAlertConfig(alertsConfigTopicName, "PUT new conf");
    }

    @PostMapping
    public void createAlertConfig(){
        alertConfigurationService.sendAlertConfig(alertsConfigTopicName, "POST new conf");
    }

    @DeleteMapping
    public void deleteAlertConfig(){
        alertConfigurationService.sendAlertConfig(alertsConfigTopicName, "DELETE new conf");
    }
}
