package com.kschool.alertplatform.enrichment.weather;

import com.kschool.alertplatform.common.model.*;
import com.kschool.alertplatform.common.model.weather.WeatherEnriched;
import com.kschool.alertplatform.common.model.weather.WeatherRaw;
import com.kschool.alertplatform.common.serdes.Serdes;
import com.kschool.alertplatform.common.utils.AlertLogger;
import com.kschool.alertplatform.common.utils.PlatformLiterals;
import com.kschool.alertplatform.common.utils.PropertyUtils;
import org.apache.kafka.streams.StreamsBuilder;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

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
                .filter((k, v) -> v != null)
                .mapValues(WeatherEnrichmentApp::toEnrichedWeather)
                .mapValues(enrichWeather -> enrichWeather.stream().map(event -> (EnrichedEvents) event).collect(Collectors.toList()))
                .selectKey((k,v) -> v.get(0).getSource())
                .to(topicNames.getProperty(PlatformLiterals.WEATHER_ENRICHED_TOPIC_NAME), Serdes.eventsEnrichedListProducer);

        return builder;
    }

    private static List<WeatherEnriched> toEnrichedWeather(WeatherRaw event) {
        List<WeatherEnriched> weatherEnrichedList = new ArrayList<>();
        weatherEnrichedList.add(new WeatherEnriched(UUID.randomUUID().toString(),
                "weather",
                "rain",
                event.getCurrently().getPrecipIntensity(),
                event.getCurrently().getIcon(),
                getCurrentTimestamp().toString()));
        weatherEnrichedList.add(new WeatherEnriched(UUID.randomUUID().toString(),
                "weather",
                "humidity",
                event.getCurrently().getHumidity() * 100,
                event.getCurrently().getIcon(),
                getCurrentTimestamp().toString()));
        weatherEnrichedList.add(new WeatherEnriched(UUID.randomUUID().toString(),
                "weather",
                "temperature",
                event.getCurrently().getTemperature(),
                event.getCurrently().getIcon(),
                getCurrentTimestamp().toString()));
        weatherEnrichedList.add(new WeatherEnriched(UUID.randomUUID().toString(),
                "weather",
                "cloudCover",
                event.getCurrently().getCloudCover(),
                event.getCurrently().getIcon(),
                getCurrentTimestamp().toString()));
        weatherEnrichedList.add(new WeatherEnriched(UUID.randomUUID().toString(),
                "weather",
                "windSpeed",
                event.getCurrently().getWindSpeed(),
                event.getCurrently().getIcon(),
                getCurrentTimestamp().toString()));

        return weatherEnrichedList;

    }

    private static Timestamp getCurrentTimestamp() {
        Date date= new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
}
