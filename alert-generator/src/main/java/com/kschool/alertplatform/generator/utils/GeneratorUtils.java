package com.kschool.alertplatform.generator.utils;

import com.kschool.alertplatform.common.model.alert.Alert;
import com.kschool.alertplatform.common.model.alert.AlertConfigAggregated;
import com.kschool.alertplatform.common.model.EnrichedEvents;
import com.kschool.alertplatform.common.utils.AlertLogger;
import org.apache.kafka.streams.KeyValue;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GeneratorUtils {

    private static AlertLogger logger = new AlertLogger(GeneratorUtils.class.getName());

    public static List<KeyValue<String, Alert>> generateAlerts(
            EnrichedEvents enrichedEvent, List<AlertConfigAggregated> alertsConfig
    ) {
        Stream<Alert> distinctAlerts =
                GeneratorUtils.applyDistinctAlertsConfig(enrichedEvent, alertsConfig);

        return distinctAlerts.
                map(alert ->
                        KeyValue.pair(alert.getUserIdToSend(), alert))
                .collect(Collectors.toList());
    }

    private static Stream<Alert> applyDistinctAlertsConfig(
            EnrichedEvents event, List<AlertConfigAggregated> alertsConfig
    ) {
        return alertsConfig.stream()
                .filter(filter -> filter.matchAllFilters(event))
                .map(alertConfig -> {
                    logSuccessMatching(alertConfig, event.getId());
                    return new Alert(
                            alertConfig.getUserId() + UUID.randomUUID().toString(),
                            "Alert received in source: " + event.getSource(),
                            "The measure: " + event.getMeasure() + ", has reached the limit " + alertConfig.getThreshold() +
                            " with value: " + event.getValue(),
                            alertConfig.getUserId());
                })
                .distinct();
    }

    private static void logSuccessMatching(AlertConfigAggregated customNotifier, String movementUuid) {
        logger.info("Alert Config: " + customNotifier.getId()
                + " for client: " + customNotifier.getUserId() + " matched movement " + movementUuid);
    }
}
