package com.kschool.alertplatform.enrichment.weather;

import com.kschool.alertplatform.common.model.*;
import com.kschool.alertplatform.common.serdes.Serdes;
import com.kschool.alertplatform.common.utils.AlertLogger;
import com.kschool.alertplatform.common.utils.PlatformLiterals;
import com.kschool.alertplatform.common.utils.PropertyUtils;
import org.apache.kafka.streams.StreamsBuilder;

import java.util.Properties;
import java.util.UUID;

import static com.kschool.alertplatform.common.utils.KafkaUtils.runKafkaStream;

public class WeatherEnrichmentApp {
    private static AlertLogger logger = new AlertLogger(WeatherEnrichmentApp.class.getName());

    public static void main(final String[] args) {
        try {

            Properties allConfig = PropertyUtils.loadConfig("/Users/n243985/alert-platform/weather-enrichment/src/main/resources/application.properties");
            final StreamsBuilder builder = buildRuleEngineTopology(allConfig);
            runKafkaStream(builder, allConfig);
        } catch (Exception exp) {
            logger.error("Error creating topology", exp);
            System.exit(-1);
        }
    }

    private static StreamsBuilder buildRuleEngineTopology(Properties topicNames) {
        final StreamsBuilder builder = new StreamsBuilder();

        builder.stream(topicNames.getProperty(PlatformLiterals.WEATHER_RAW_TOPIC_NAME), Serdes.weatherRawConsumer)
                .mapValues(WeatherEnrichmentApp::toEnrichedWeather)
                .mapValues(event -> (EnrichedEvents) event)
                .to(topicNames.getProperty(PlatformLiterals.WEATHER_ENRICHED_TOPIC_NAME), Serdes.eventsEnrichedProducer);

        return builder;
    }

    private static WeatherEnriched toEnrichedWeather(WeatherRaw event) {
        return new WeatherEnriched(UUID.randomUUID().toString(),
                40.54,
                "weather processed",
                "rain processed",
                event.getRainAmount(),
                "mimedia");
    }
}
