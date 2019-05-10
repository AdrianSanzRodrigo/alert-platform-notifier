package com.kschool.alertstreaming;

import com.kschool.alertstreaming.model.AlertConfig;
import com.kschool.alertstreaming.model.TransUtils;
import com.kschool.alertstreaming.utils.AlertLogger;
import com.kschool.alertstreaming.utils.PropertyUtils;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import static com.kschool.alertstreaming.utils.KafkaUtils.*;

public class AlertStreamingApplication {

    static AlertLogger logger = new AlertLogger(AlertStreamingApplication.class.getName());

    public static void main(final String[] args) {
        try {
            Properties streamsConfiguration = PropertyUtils.loadConfig("/Users/n243985/alert-platform/alert-streaming/src/main/resources/application.properties");
            final StreamsBuilder builder = buildTopology(
                    streamsConfiguration.getProperty("alerts-config.topic.name"),
                    streamsConfiguration.getProperty("aggregated.alerts-config.topic.name"));
            runKafkaStream(builder, streamsConfiguration);
        } catch (Exception e) {
            System.exit(-1);
        }
    }

    private static void runKafkaStream(StreamsBuilder builder, Properties kafkaConfig) {
        final KafkaStreams streams = new KafkaStreams(builder.build(), kafkaConfig);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private static StreamsBuilder buildTopology(String inputTopic, String outputTopic) {
        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic, alertsConfigConsumer)
                .groupByKey().aggregate(
                HashMap::new,
                (key, value, aggregate) -> updateOrLogError(aggregate, value),
                tableMaterialization)
                .toStream()
                .mapValues(customNotifierOperation -> customNotifierOperation.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList()))
                .mapValues(customNotifiers -> customNotifiers.stream().map(TransUtils::createFromReadModel).collect(Collectors.toList()))
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
