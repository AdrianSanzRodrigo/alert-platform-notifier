package com.kschool.alertplatform.common.utils;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import com.kschool.alertplatform.common.model.WeatherEnriched;
import com.kschool.alertplatform.common.serdes.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Properties;

public class KafkaUtils {

    static AlertLogger logger = new AlertLogger(KafkaUtils.class.getName());

    public static void runKafkaStream(StreamsBuilder builder, Properties kafkaConfig) {
        final KafkaStreams streams = new KafkaStreams(builder.build(), kafkaConfig);
        streams.cleanUp();
        streams.start();
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    public static KStream<String, EnrichedEvents> getAllEnrichedEventsTopology(
            StreamsBuilder builder,
            String enrichedAirQualityTopicName,
            String enrichedTrafficDensityTopicName,
            String enrichedWeatherTopicName
    ) {

        KStream<String, EnrichedEvents> enrichedAirQualityStream =
                builder.stream(enrichedAirQualityTopicName, Serdes.airQualityEnrichedConsumer)
                        .filter((key, event) -> event != null)
                        .mapValues(event -> event);

        KStream<String, EnrichedEvents> enrichedTrafficDensityStream =
                builder.stream(enrichedTrafficDensityTopicName, Serdes.trafficDensityEnrichedConsumer)
                        .filter((key, event) -> event != null)
                        .mapValues(event -> event);

        KStream<String, EnrichedEvents> enrichedWeatherStream =
                builder.stream(enrichedWeatherTopicName, Serdes.weatherEnrichedConsumer)
                        .mapValues(event -> event);

        return enrichedAirQualityStream.merge(enrichedTrafficDensityStream).merge(enrichedWeatherStream);
    }
}
