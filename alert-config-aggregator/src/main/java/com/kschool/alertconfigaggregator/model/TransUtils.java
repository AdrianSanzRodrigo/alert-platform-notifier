package com.kschool.alertconfigaggregator.model;

public class TransUtils {


    // Using null instead of optional because kafka streams do not operate with Optionals.
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
}
