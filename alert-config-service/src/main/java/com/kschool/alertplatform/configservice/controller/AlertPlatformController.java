package com.kschool.alertplatform.configservice.controller;

import com.kschool.alertplatform.common.model.alert.AlertConfig;
import com.kschool.alertplatform.security.domain.User;
import com.kschool.alertplatform.configservice.service.AlertConfigurationService;
import com.kschool.alertplatform.configservice.service.AlertNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kschool.alertplatform.configservice.utils.APILiterals.*;

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
    private String alertConfigsTopicName;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<List<AlertConfig>> getAlertConfig(@AuthenticationPrincipal final User user) {
        final List<AlertConfig> alertConfigs = alertConfigurationService.findAlertConfigs(user.getClientId());
        return new ResponseEntity<>(alertConfigs, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<List<AlertConfig>> createAlertConfig(@AuthenticationPrincipal final User user,
                                                               @RequestBody List<AlertConfig> alertConfigs){
        List<AlertConfig> alertConfigsSeted = alertConfigurationService.setAlertConfigFields(alertConfigs, user.getClientId(), POST_ACTION);
        alertConfigurationService.insertAlertConfigs(alertConfigsSeted, user.getClientId());
        alertConfigurationService.sendAlertConfig(alertConfigsTopicName, alertConfigsSeted, user.getClientId());
        return new ResponseEntity<>(alertConfigsSeted, HttpStatus.OK);
    }

    @PutMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String>  updateAlertConfig(@AuthenticationPrincipal final User user,
                                                     @RequestBody AlertConfig alertConfig,
                                                     @PathVariable final String uuid){
        AlertConfig alertConfigSeted = alertConfigurationService.setAlertConfigFields(alertConfig, uuid, user.getClientId(), PUT_ACTION);
        boolean isPresent = alertConfigurationService.isAlertConfigPresent(alertConfigSeted, user.getClientId());
        if (isPresent) {
            alertConfigurationService.updateAlert(alertConfig, user.getClientId());
            alertConfigurationService.sendAlertConfig(alertConfigsTopicName, alertConfigSeted, user.getClientId());
            return new ResponseEntity<>("ok", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Document Id: " + user.getClientId() +
                    " or alertConfigId: " + uuid + " does not exist", HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String> deleteAlertConfig(@AuthenticationPrincipal final User user,
                                                    @PathVariable final String uuid){
        AlertConfig alertConfigToDelete = alertConfigurationService.getAlertConfigById(uuid, user.getClientId());
        AlertConfig alertConfigSeted = alertConfigurationService.setAlertConfigFields(alertConfigToDelete, uuid, user.getClientId(), DELETE_ACTION);
        alertConfigurationService.deleteAlert(alertConfigSeted, user.getClientId());
        alertConfigurationService.sendAlertConfig(alertConfigsTopicName, alertConfigSeted, user.getClientId());
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
