package com.kschool.alertplatform.configaggregator.utils;

import com.kschool.alertplatform.common.model.AlertConfig;
import com.kschool.alertplatform.common.model.AlertConfigAggregated;

import java.util.Map;
import java.util.Objects;

public class AggregationUtils {

    public static AlertConfigAggregated createFromReadModel(AlertConfig readModel) {
        return AlertConfigAggregated.builder()
                .id(readModel.getId() + " processed")
                .userId(readModel.getUserId() + " processed")
                .limitType(readModel.getLimitType() + " processed")
                .measure(readModel.getMeasure() + " processed")
                .source(readModel.getSource() + " processed")
                .threshold(readModel.getThreshold() + " processed")
                .timestamp(readModel.getTimestamp() + " processed").build();
    }

    public static void modifyCustomAlerts(
            Map<String, AlertConfig> userAlerts, AlertConfig modification
    ) {
        if(Objects.equals(modification.getAction().toLowerCase(), "delete")
                && userAlerts.containsKey(modification.getId()))
            userAlerts.remove(modification.getId());

        else if(Objects.equals(modification.getAction().toLowerCase(), "update")
                && userAlerts.containsKey(modification.getId()))
            userAlerts.replace(modification.getId(), modification);

        else if(Objects.equals(modification.getAction().toLowerCase(), "create")
                && !userAlerts.containsKey(modification.getId()))
            userAlerts.putIfAbsent(modification.getId(), modification);

        else if(Objects.equals(modification.getAction().toLowerCase(), "delete_all"))
            userAlerts.clear();

        else throw new IllegalStateException("Invalid operation: " + modification.getAction());
    }
}
