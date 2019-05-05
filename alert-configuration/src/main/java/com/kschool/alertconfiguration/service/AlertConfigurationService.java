package com.kschool.alertconfiguration.service;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kschool.alertconfiguration.dao.CouchbaseDAO;
import com.kschool.alertconfiguration.exceptions.ValidationException;
import com.kschool.alertconfiguration.model.AlertConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlertConfigurationService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private CouchbaseDAO couchbaseDAO;

    private static final Gson gson = new GsonBuilder().create();

    public void sendAlertConfig(String topic, List<AlertConfig> alertConfigs, String clientId) {
        alertConfigs.forEach(alertConfig -> sendAlertConfig(topic, alertConfig, clientId));
    }

    public void sendAlertConfig(String topic, AlertConfig alertConfig, String clientId) {
        kafkaTemplate.send(topic, clientId, alertConfig);
    }

    public List<AlertConfig> findAlertConfigs(String id) {
        return couchbaseDAO.findAlertConfigs(id);
    }

    public AlertConfig getAlertConfigById(String alertId, String userId) {
        return findAlertConfigs(userId)
                .stream().filter(alertConfigToDelete -> alertConfigToDelete.getId().equals(alertId))
                .findFirst().orElseThrow(() -> new ValidationException(ValidationException.ID_NOT_EXIST_ERROR + alertId));
    }

    public void insertAlertConfigs(List<AlertConfig> alertConfigs, String docId) {
        alertConfigs.forEach(alertConfig -> {

            if (couchbaseDAO.getBucket().exists(docId) && !isAlertConfigIdPresent(alertConfig, docId)) {
                insertAlertConfig(alertConfig, docId);
            } else if (!couchbaseDAO.getBucket().exists(docId)) {
                insertAlertConfigInNewDoc(alertConfig, docId);
            }
        });
    }

    private void insertAlertConfigInNewDoc(AlertConfig alertConfig, String docId) {
        final JsonObject alertObject = JsonObject.fromJson(gson.toJson(alertConfig));
        final JsonObject placeholderValues = JsonObject.create()
                .put("docId", docId);

        N1qlQuery nq = N1qlQuery.parameterized(couchbaseDAO.getInsertQuery(alertObject), placeholderValues);
        couchbaseDAO.getBucket().query(nq);
    }

    private void insertAlertConfig(AlertConfig alertConfig, String docId) {
        couchbaseDAO.getBucket().mutateIn(docId)
                .arrayAppendAll("alertConfigs", alertConfig)
                .execute();
    }

    public void updateAlert(AlertConfig alert, String docId) {
        final JsonObject placeholderValues = JsonObject.create()
                .put("alertId", alert.getId())
                .put("docId", docId)
                .put("threshold", alert.getThreshold())
                .put("limitType", alert.getLimitType());

        N1qlQuery nq = N1qlQuery.parameterized(couchbaseDAO.getUpdateQuery(), placeholderValues);
        couchbaseDAO.getBucket().query(nq);
    }

    public void deleteAlert(AlertConfig alert, String docId) {
        final JsonObject placeholderValues = JsonObject.create()
                .put("id", alert.getId())
                .put("docId", docId);

        N1qlQuery nq = N1qlQuery.parameterized(String.valueOf(couchbaseDAO.getDeleteQuery()), placeholderValues);
        couchbaseDAO.getBucket().query(nq);
    }

    private boolean isAlertConfigIdPresent(AlertConfig alert, String docId) {
        final JsonObject placeholderValues = JsonObject.create()
                .put("alertId", alert.getId())
                .put("docId", docId);

        N1qlQuery nq = N1qlQuery.parameterized(couchbaseDAO.getAlertIdPresenceQuery(), placeholderValues);

        return (Boolean) couchbaseDAO.getBucket().query(nq).allRows().get(0).value().get("isPresent");
    }

    public boolean isAlertConfigPresent(AlertConfig alertConfig, String clientId) {
        return couchbaseDAO.getBucket().exists(clientId) && isAlertConfigIdPresent(alertConfig, clientId);
    }

    public List<AlertConfig> setAlertConfigFields(List<AlertConfig> alertConfigs, String action) {
        return alertConfigs.stream().map(
                alertConfig -> {
                    final String alertIdToSet = alertConfig.getSource() + "_" + UUID.randomUUID().toString();
                    return setAlertConfigFields(alertConfig, alertIdToSet, action);
                }
        ).collect(Collectors.toList());
    }

    public AlertConfig setAlertConfigFields(AlertConfig alertConfig, String alertId, String action) {
        alertConfig.setId(alertId);
        alertConfig.setTimestamp(getCurrentTimestamp().toString());
        alertConfig.setAction(action);
        return alertConfig;
    }

    private Timestamp getCurrentTimestamp() {
        Date date= new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
}
