package com.kschool.alertplatform.generator;

import com.kschool.alertplatform.common.model.alert.Alert;
import com.kschool.alertplatform.common.model.alert.AlertConfigAggregated;
import com.kschool.alertplatform.common.serdes.Serdes;
import com.kschool.alertplatform.common.utils.AlertLogger;
import com.kschool.alertplatform.common.utils.PlatformLiterals;
import com.kschool.alertplatform.common.utils.PropertyUtils;
import com.kschool.alertplatform.common.model.EnrichedEvents;
import com.kschool.alertplatform.generator.utils.GeneratorUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import static com.kschool.alertplatform.common.serdes.Serdes.*;
import static com.kschool.alertplatform.common.utils.KafkaUtils.getAllEnrichedEventsTopology;
import static com.kschool.alertplatform.common.utils.KafkaUtils.runKafkaStream;

public class AlertGeneratorApp {

    static AlertLogger logger = new AlertLogger(AlertGeneratorApp.class.getName());

    public static void main(final String[] args) {
        try {

            Properties allConfig = PropertyUtils.loadConfig("/Users/n243985/alert-platform/alert-generator/src/main/resources/application.properties");
            final StreamsBuilder builder = buildRuleEngineTopology(allConfig);
            runKafkaStream(builder, allConfig);
        } catch (Exception exp) {
            logger.error("Error creating topology", exp);
            System.exit(-1);
        }
    }

    private static StreamsBuilder buildRuleEngineTopology(Properties topicNames) {
        final StreamsBuilder builder = new StreamsBuilder();

        KTable<String, List<AlertConfigAggregated>> filtersByMeasure =
                builder.table(topicNames.getProperty(PlatformLiterals.AGGREGATED_ALERTS_CONFIG_TOPIC_NAME), alertsConfigAggregatedConsumer);

        filtersByMeasure.toStream().peek((key, value) -> logger.info("Filters key:" + key + " value: " + value));

        KStream<String, EnrichedEvents> enrichedEvents = getAllEnrichedEventsTopology(
                builder,
                topicNames.getProperty(PlatformLiterals.AIR_QUALITY_ENRICHED_TOPIC_NAME),
                topicNames.getProperty(PlatformLiterals.TRAFFIC_DENSITY_ENRICHED_TOPIC_NAME),
                topicNames.getProperty(PlatformLiterals.WEATHER_ENRICHED_TOPIC_NAME))
                .filter((k, v) -> v != null);

        enrichedEvents.peek((key, value) -> logger.info("Enriched event key:" + key + " value: " + value));

        KStream<String, Alert> alertsToSend = enrichedEvents.join(
                filtersByMeasure,
                GeneratorUtils::generateAlerts
        ).flatMap((key, value) -> value);

        alertsToSend.to(topicNames.getProperty(PlatformLiterals.ALERTS_TOPIC_NAME), alertProducer);

        return builder;
    }
}
