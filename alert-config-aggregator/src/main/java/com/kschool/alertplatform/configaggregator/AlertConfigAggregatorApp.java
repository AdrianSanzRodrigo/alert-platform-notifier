package com.kschool.alertplatform.configaggregator;

import com.kschool.alertplatform.common.utils.PlatformLiterals;
import com.kschool.alertplatform.configaggregator.utils.AggregationUtils;
import com.kschool.alertplatform.common.model.alert.AlertConfig;
import com.kschool.alertplatform.common.utils.AlertLogger;
import com.kschool.alertplatform.common.utils.PropertyUtils;
import org.apache.kafka.streams.StreamsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.kschool.alertplatform.common.serdes.Serdes.alertsConfigAggregatedProducer;
import static com.kschool.alertplatform.common.serdes.Serdes.alertsConfigConsumer;
import static com.kschool.alertplatform.common.serdes.Serdes.tableMaterialization;
import static com.kschool.alertplatform.common.utils.KafkaUtils.runKafkaStream;
import static com.kschool.alertplatform.configaggregator.utils.AggregationUtils.modifyCustomAlerts;

public class AlertConfigAggregatorApp {

    static AlertLogger logger = new AlertLogger(AlertConfigAggregatorApp.class.getName());

    public static void main(final String[] args) {
        try {
            Properties streamsConfiguration = PropertyUtils.loadConfig("/Users/n243985/alert-platform/alert-config-aggregator/src/main/resources/application.properties");
            final StreamsBuilder builder = buildTopology(
                    streamsConfiguration.getProperty(PlatformLiterals.ALERTS_CONFIG_TOPIC_NAME),
                    streamsConfiguration.getProperty(PlatformLiterals.AGGREGATED_ALERTS_CONFIG_TOPIC_NAME));
            runKafkaStream(builder, streamsConfiguration);
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    private static StreamsBuilder buildTopology(String inputTopic, String outputTopic) {
        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, alertsConfigConsumer)
                .peek((k,v) -> logger.info("New filter in measure: " + v.getMeasure()
                        + " with action: " + v.getAction()
                        + " and threshold: " + v.getThreshold()))
                .groupByKey().aggregate(
                HashMap::new,
                (key, value, aggregate) -> updateOrLogError(aggregate, value),
                tableMaterialization)
                .toStream()
                .mapValues(customNotifierOperation -> new ArrayList<>(customNotifierOperation.values()))
                .mapValues(customNotifiers -> customNotifiers.stream().map(AggregationUtils::createFromReadModel).collect(Collectors.toList()))
                .to(outputTopic, alertsConfigAggregatedProducer);
        return builder;
    }

    private static Map<String, AlertConfig> updateOrLogError(
            Map<String, AlertConfig> userAlerts, AlertConfig modification
    ) {
        try {
            modifyCustomAlerts(userAlerts, modification);
        } catch (Exception e) {
            logger.error("Error updating: ", e);
            System.exit(-1);
        }
        return userAlerts;
    }
}
