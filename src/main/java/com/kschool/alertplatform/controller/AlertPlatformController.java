package com.kschool.alertplatform.controller;

import com.kschool.alertplatform.model.AlertConfig;
import com.kschool.alertplatform.model.AlertsConfigDoc;
import com.kschool.alertplatform.security.domain.User;
import com.kschool.alertplatform.service.AlertConfigurationService;
import com.kschool.alertplatform.service.AlertNotifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.kschool.alertplatform.utils.APILiterals.*;

@RestController
@RequestMapping("/alerts")
public class AlertPlatformController {

    @Autowired
    private AlertConfigurationService alertConfigurationService;

    @Autowired
    private AlertNotifierService alertNotifierService;

    @Autowired
    private CouchbaseTemplate cbTemplate;

    @Value(value = "${alerts.topic.name}")
    private String alertsTopicName;

    @Value(value = "${alerts-config.topic.name}")
    private String alertsConfigTopicName;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<List<AlertConfig>> getAlertConfig(@AuthenticationPrincipal final User user) {
        final List<AlertConfig> alertsConfig = alertConfigurationService.findAlertsConfig(user.getClientId());
        return new ResponseEntity<>(alertsConfig, HttpStatus.OK);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<List<AlertConfig>> createAlertConfig(@AuthenticationPrincipal final User user,
                                                               @RequestBody List<AlertConfig> alertConfigs){
        List<AlertConfig> alertsConfigSeted = alertConfigurationService.setAlertConfigFields(alertConfigs, user, POST_ACTION);
        AlertsConfigDoc alertsConfigDoc = new AlertsConfigDoc();
        alertsConfigDoc.setAlertConfigs(alertsConfigSeted);
        alertsConfigDoc.setId(user.getClientId());
        cbTemplate.save(alertsConfigDoc);
        //alertConfigurationService.sendAlertConfig(alertsConfigTopicName, alertsConfigSeted);
        return new ResponseEntity<>(alertsConfigSeted, HttpStatus.OK);
    }

    @PutMapping(value = "/{uuid}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity<String>  updateAlertConfig(@AuthenticationPrincipal final User user,
                                                     @RequestBody AlertConfig alertConfig,
                                                     @PathVariable final String uuid){
        AlertConfig alertConfigSeted = alertConfigurationService.setAlertConfigFields(alertConfig, uuid, PUT_ACTION);
        boolean isPresent = alertConfigurationService.isAlertPresent(alertConfigSeted, user.getClientId());
        if (cbTemplate.getCouchbaseBucket().exists(user.getClientId()) && isPresent) {
            alertConfigurationService.updateAlert(alertConfig, user.getClientId());
            //alertConfigurationService.sendAlertConfig(alertsConfigTopicName, alertConfigConfigSeted);
            return new ResponseEntity<>("ok", HttpStatus.CREATED);
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
        AlertConfig alertConfigConfigSeted = alertConfigurationService.setAlertConfigFields(alertConfigToDelete, uuid, DELETE_ACTION);
        alertConfigurationService.sendAlertConfig(alertsConfigTopicName, alertConfigConfigSeted);
        return new ResponseEntity<>("ok", HttpStatus.OK);
    }
}
