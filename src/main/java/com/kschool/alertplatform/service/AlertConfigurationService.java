package com.kschool.alertplatform.service;

import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlQuery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kschool.alertplatform.exceptions.AlertConfigNotFoundException;
import com.kschool.alertplatform.exceptions.ValidationException;
import com.kschool.alertplatform.model.AlertConfig;
import com.kschool.alertplatform.model.AlertsConfigDoc;
import com.kschool.alertplatform.security.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AlertConfigurationService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private CouchbaseTemplate cbTemplate;

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    public void sendAlertConfig(String topic, List<AlertConfig> alertConfigs) {
        alertConfigs.forEach(alertConfig -> sendAlertConfig(topic, alertConfig));
    }

    public void sendAlertConfig(String topic, AlertConfig alertConfig) {
        kafkaTemplate.send(topic, alertConfig.getId(), alertConfig);
    }

    public List<AlertConfig> findAlertsConfig(String id) {
        return Optional.ofNullable(cbTemplate.findById(id, AlertsConfigDoc.class).getAlertConfigs()).orElseThrow(AlertConfigNotFoundException::new);
    }

    public AlertConfig getAlertConfigById(String alertId, String userId) {
        return findAlertsConfig(userId)
                .stream().filter(alertConfigToDelete -> alertConfigToDelete.getId().equals(alertId))
                .findFirst().orElseThrow(() -> new ValidationException(ValidationException.ID_NOT_EXIST_ERROR + alertId));
    }

    public List<AlertConfig> setAlertConfigFields(List<AlertConfig> alertConfigs, User user, String action) {
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

    private void insertAlertInNewDoc(AlertConfig alertConfig) {
        final JsonObject alertObject = JsonObject.fromJson(gson.toJson(alertConfig));
        final JsonObject placeholderValues = JsonObject.create()
                .put("docId", alertConfig.getId());

        N1qlQuery nq = N1qlQuery.parameterized(getInsertQuery(alertObject), placeholderValues);
        cbTemplate.getCouchbaseBucket().query(nq);
    }

    public void updateAlert(AlertConfig alert, String docId) {
        final JsonObject placeholderValues = JsonObject.create()
                .put("alertId", alert.getId())
                .put("docId", docId)
                .put("threshold", alert.getThreshold())
                .put("limitType", alert.getLimitType());

        N1qlQuery nq = N1qlQuery.parameterized(getUpdateQuery(), placeholderValues);
        cbTemplate.getCouchbaseBucket().query(nq);
    }

    private String getInsertQuery(JsonObject alertObject) {
        return "INSERT INTO " + cbTemplate.getCouchbaseBucket().name() +
                " (KEY, VALUE) VALUES ( $docId," +
                " {[" +
                alertObject + "]})";
    }

    public boolean isAlertPresent(AlertConfig alert, String docId) {
        final JsonObject placeholderValues = JsonObject.create()
                .put("alertId", alert.getId())
                .put("docId", docId);

        N1qlQuery nq = N1qlQuery.parameterized(getAlertIdPresenceQuery(), placeholderValues);

        return (Boolean) cbTemplate.getCouchbaseBucket().query(nq).allRows().get(0).value().get("isPresent");
    }

    private String getUpdateQuery() {
        return "UPDATE " + "`" + cbTemplate.getCouchbaseBucket().name() + "`" +
                " USE KEYS $docId" +
                " SET alertConfig.threshold = $threshold FOR alertConfig IN alertConfigs WHEN alertConfig.id = $alertId END, " +
                "alertConfig.limitType = $limitType FOR alertConfig IN alertConfigs WHEN alertConfig.id = $alertId END " +
                "RETURNING alertConfigs";
    }

    private String getAlertIdPresenceQuery() {
        return "SELECT CASE WHEN st.isPresent IS NULL THEN FALSE " +
                "ELSE st.isPresent END AS isPresent FROM " +
                "(SELECT ARRAY_CONTAINS(idList, $alertId) isPresent FROM(" +
                "SELECT ARRAY_AGG(alertConfig.id) as idList FROM " +
                "`" + cbTemplate.getCouchbaseBucket().name() + "`" + " USE KEYS $docId" +
                " UNNEST alertConfigs alertConfig) sq) st";
    }

    private Timestamp getCurrentTimestamp() {
        Date date= new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
}
