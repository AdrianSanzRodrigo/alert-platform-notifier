package com.kschool.alertplatform.configservice.dao;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.kschool.alertplatform.common.model.alert.AlertConfig;
import com.kschool.alertplatform.configservice.exceptions.AlertConfigNotFoundException;
import com.kschool.alertplatform.configservice.model.AlertConfigsDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CouchbaseDAO {

    @Autowired
    private CouchbaseTemplate cbTemplate;

    public List<AlertConfig> findAlertConfigs(String id) {
        return Optional.ofNullable(cbTemplate.findById(id, AlertConfigsDoc.class).getAlertConfigs())
                .orElseThrow(AlertConfigNotFoundException::new);
    }

    public String getInsertQuery(JsonObject alertObject) {
        return "INSERT INTO " + "`" + getBucket().name() + "`" +
                " (KEY, VALUE) VALUES ( $docId," +
                " { \"alertConfigs\" :[" +
                alertObject + "]})";
    }

    public String getUpdateQuery() {
        return "UPDATE " + "`" + getBucket().name() + "`" +
                " USE KEYS $docId" +
                " SET alertConfig.threshold = $threshold FOR alertConfig IN alertConfigs WHEN alertConfig.id = $alertId END, " +
                "alertConfig.limitType = $limitType FOR alertConfig IN alertConfigs WHEN alertConfig.id = $alertId END " +
                "RETURNING alertConfigs";
    }

    public String getAlertIdPresenceQuery() {
        return "SELECT CASE WHEN st.isPresent IS NULL THEN FALSE " +
                "ELSE st.isPresent END AS isPresent FROM " +
                "(SELECT ARRAY_CONTAINS(idList, $alertId) isPresent FROM(" +
                "SELECT ARRAY_AGG(alertConfig.id) as idList FROM " +
                "`" + getBucket().name() + "`" + " USE KEYS $docId" +
                " UNNEST alertConfigs alertConfig) sq) st";
    }

    public String getMeasurePresenceQuery() {
        return "SELECT CASE WHEN st.isPresent IS NULL THEN FALSE " +
                "ELSE st.isPresent END AS isPresent FROM " +
                "(SELECT ARRAY_CONTAINS(measureList, $measure) isPresent FROM(" +
                "SELECT ARRAY_AGG(alertConfig.measure) as measureList FROM " +
                "`" + getBucket().name() + "`" + " USE KEYS $docId" +
                " UNNEST alertConfigs alertConfig) sq) st";
    }



    public String getDeleteQuery() {
        return "UPDATE " +  "`" + getBucket().name() + "`" +
                " USE KEYS $docId" +
                " SET alertConfigs = ARRAY alertConfig FOR alertConfig IN alertConfigs WHEN " +
                "alertConfig.id != $id END " +
                "RETURNING alertConfigs";
    }

    public Bucket getBucket() {
        return cbTemplate.getCouchbaseBucket();
    }
}
