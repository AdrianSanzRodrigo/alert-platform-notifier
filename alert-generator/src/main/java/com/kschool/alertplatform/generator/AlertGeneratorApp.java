package com.kschool.alertplatform.generator;

import com.kschool.alertplatform.common.model.Alert;
import com.kschool.alertplatform.common.model.AlertConfigAggregated;
import com.kschool.alertplatform.common.utils.AlertLogger;
import com.kschool.alertplatform.common.utils.PropertyUtils;
import com.kschool.alertplatform.common.model.EnrichedEvents;
import com.kschool.alertplatform.generator.utils.GeneratorUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;

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

        KTable<String, List<AlertConfigAggregated>> filtersByUserId =
                builder.table(topicNames.getProperty("aggregated.alerts-config.topic.name"), alertsConfigAggregatedConsumer);

        logger.info("Es queryable: " + filtersByUserId.queryableStoreName());
        filtersByUserId.toStream().peek((key, value) ->logger.info("Filters key:" + key + " value: " + value));

        KStream<String, EnrichedEvents> enrichedEvents = getAllEnrichedEventsTopology(
                builder,
                topicNames.getProperty("enriched.air-quality.topic.name"),
                topicNames.getProperty("enriched.traffic-density.topic.name"),
                topicNames.getProperty("enriched.weather.topic.name"));

        enrichedEvents.peek((key, value) ->logger.info("Enriched event key:" + key + " value: " + value));

        KStream<String, Alert> alertsToSend = enrichedEvents.join(
                filtersByUserId,
                GeneratorUtils::generateAlerts
        ).flatMap((key, value) -> value);

        alertsToSend.to(topicNames.getProperty("alerts.topic.name"), alertProducer);

        return builder;
    }
}
