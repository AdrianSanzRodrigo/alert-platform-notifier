package com.kschool.alertplatform.controller;

import com.kschool.alertplatform.service.AlertConfigurationService;
import com.kschool.alertplatform.service.AlertNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/alerts")
public class AlertPlatformController {

    @Autowired
    private AlertConfigurationService alertConfigurationService;


    @GetMapping
    public String getAlertConfig() {
        return "I'm getting alert config";
    }

    @PutMapping
    public void updateAlertConfig(){
        alertConfigurationService.sendNewConfig("PUT new conf");
    }

    @PostMapping
    public void createAlertConfig(){
        alertConfigurationService.sendNewConfig("POST new conf");
    }

    @DeleteMapping
    public void deleteAlertConfig(){
        alertConfigurationService.sendNewConfig("DELETE new conf");
    }
}
