package com.kschool.alertplatform.configaggregator.utils;

import com.kschool.alertplatform.common.model.alert.AlertConfig;
import com.kschool.alertplatform.common.model.alert.AlertConfigAggregated;

import java.util.Map;
import java.util.Objects;

public class AggregationUtils {

    public static AlertConfigAggregated createFromReadModel(AlertConfig readModel) {
        return AlertConfigAggregated.builder()
                .id(readModel.getId())
                .userId(readModel.getUserId())
                .limitType(readModel.getLimitType())
                .measure(readModel.getMeasure())
                .source(readModel.getSource())
                .threshold(readModel.getThreshold())
                .timestamp(readModel.getTimestamp()).build();
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
