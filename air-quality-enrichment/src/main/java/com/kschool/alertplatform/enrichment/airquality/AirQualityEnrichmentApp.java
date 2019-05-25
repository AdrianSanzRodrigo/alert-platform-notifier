package com.kschool.alertplatform.enrichment.airquality;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import com.kschool.alertplatform.common.model.airquality.AirQualityEnriched;
import com.kschool.alertplatform.common.model.airquality.AirQualityRaw;
import com.kschool.alertplatform.common.model.weather.WeatherEnriched;
import com.kschool.alertplatform.common.model.weather.WeatherRaw;
import com.kschool.alertplatform.common.serdes.Serdes;
import com.kschool.alertplatform.common.utils.AlertLogger;
import com.kschool.alertplatform.common.utils.PlatformLiterals;
import com.kschool.alertplatform.common.utils.PropertyUtils;
import com.kschool.alertplatform.enrichment.airquality.com.kschool.aletplatform.enrichment.airquality.utils.AirQualityUtils;
import org.apache.kafka.streams.StreamsBuilder;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.kschool.alertplatform.common.utils.KafkaUtils.runKafkaStream;

public class AirQualityEnrichmentApp {
    private static AlertLogger logger = new AlertLogger(AirQualityEnrichmentApp.class.getName());

    public static void main(final String[] args) {
        try {

            Properties allConfig = PropertyUtils.loadConfig("/Users/n243985/alert-platform/air-quality-enrichment/src/main/resources/application.properties");
            final StreamsBuilder builder = buildRuleEngineTopology(allConfig);
            runKafkaStream(builder, allConfig);
        } catch (Exception exp) {
            logger.error("Error creating topology", exp);
            System.exit(-1);
        }
    }

    private static StreamsBuilder buildRuleEngineTopology(Properties topicNames) {
        final StreamsBuilder builder = new StreamsBuilder();

        builder.stream(topicNames.getProperty(PlatformLiterals.AIR_QUALITY_RAW_TOPIC_NAME), Serdes.airQualityRawConsumer)
                .filter((k, v) -> v != null)
                .mapValues(AirQualityEnrichmentApp::toEnrichedAirQuality)
                .mapValues(airQuality -> airQuality.stream().map(event -> (EnrichedEvents) event).collect(Collectors.toList()))
                .selectKey((k,v) -> v.get(0).getSource())
                .to(topicNames.getProperty(PlatformLiterals.AIR_QUALITY_ENRICHED_TOPIC_NAME), Serdes.eventsEnrichedListProducer);

        return builder;
    }

    private static List<AirQualityEnriched> toEnrichedAirQuality(AirQualityRaw event) {
        Map<String, String> magnitudeDict = AirQualityUtils.getMagnitudeDict();
        Map<String, String> stationDict = AirQualityUtils.getStationDict();

        List<AirQualityEnriched> airQualityEnrichedList = new ArrayList<>();
        airQualityEnrichedList.add(new AirQualityEnriched(UUID.randomUUID().toString(),
                "airQuality",
                magnitudeDict.get(event.getMAGNITUD()),
                AirQualityUtils.getCorrespondingValue(event),
                stationDict.get(event.getPUNTO_MUESTREO().substring(0,8)),
                getCurrentTimestamp().toString()));
        return airQualityEnrichedList;

    }

    private static Timestamp getCurrentTimestamp() {
        Date date= new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
}
