package com.kschool.alertplatform.enrichment.trafficdensity;

import com.kschool.alertplatform.common.model.EnrichedEvents;
import com.kschool.alertplatform.common.model.trafficdensity.TrafficDensityEnriched;
import com.kschool.alertplatform.common.model.trafficdensity.TrafficDensityRaw;
import com.kschool.alertplatform.common.serdes.Serdes;
import com.kschool.alertplatform.common.utils.AlertLogger;
import com.kschool.alertplatform.common.utils.PlatformLiterals;
import com.kschool.alertplatform.common.utils.PropertyUtils;
import org.apache.kafka.streams.StreamsBuilder;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static com.kschool.alertplatform.common.utils.KafkaUtils.runKafkaStream;

public class TrafficDensityEnrichmentApp {
    private static AlertLogger logger = new AlertLogger(TrafficDensityEnrichmentApp.class.getName());

    public static void main(final String[] args) {
        try {

            Properties allConfig = PropertyUtils.loadConfig("/Users/n243985/alert-platform/traffic-density-enrichment/src/main/resources/application.properties");
            final StreamsBuilder builder = buildRuleEngineTopology(allConfig);
            runKafkaStream(builder, allConfig);
        } catch (Exception exp) {
            logger.error("Error creating topology", exp);
            System.exit(-1);
        }
    }

    private static StreamsBuilder buildRuleEngineTopology(Properties topicNames) {
        final StreamsBuilder builder = new StreamsBuilder();

        builder.stream(topicNames.getProperty(PlatformLiterals.TRAFFIC_DENSITY_RAW_TOPIC_NAME), Serdes.trafficDensityRawConsumer)
                .filter((k, v) -> v != null)
                .mapValues(TrafficDensityEnrichmentApp::toEnrichedTrafficDensity)
                .mapValues(trafficDensity -> trafficDensity.stream().map(event -> (EnrichedEvents) event).collect(Collectors.toList()))
                .selectKey((k,v) -> v.get(0).getSource())
                .to(topicNames.getProperty(PlatformLiterals.TRAFFIC_DENSITY_ENRICHED_TOPIC_NAME), Serdes.eventsEnrichedListProducer);

        return builder;
    }

    private static List<TrafficDensityEnriched> toEnrichedTrafficDensity(TrafficDensityRaw event) {
        List<TrafficDensityEnriched> trafficDensityEnrichedList = new ArrayList<>();
        trafficDensityEnrichedList.add(new TrafficDensityEnriched(UUID.randomUUID().toString(),
                "trafficDensity",
                event.getDatoGlobal().get(0).getNombre(),
                Double.parseDouble(event.getDatoGlobal().get(0).getVALOR()),
                getCurrentTimestamp().toString()));
        trafficDensityEnrichedList.add(new TrafficDensityEnriched(UUID.randomUUID().toString(),
                "trafficDensity",
                event.getDatoGlobal().get(1).getNombre(),
                Double.parseDouble(event.getDatoGlobal().get(1).getVALOR()),
                getCurrentTimestamp().toString()));
        trafficDensityEnrichedList.add(new TrafficDensityEnriched(UUID.randomUUID().toString(),
                "trafficDensity",
                event.getDatoGlobal().get(2).getNombre(),
                Double.parseDouble(event.getDatoGlobal().get(2).getVALOR()),
                getCurrentTimestamp().toString()));
        trafficDensityEnrichedList.add(new TrafficDensityEnriched(UUID.randomUUID().toString(),
                "trafficDensity",
                event.getDatoGlobal().get(3).getNombre(),
                Double.parseDouble(event.getDatoGlobal().get(3).getVALOR()),
                getCurrentTimestamp().toString()));

        return trafficDensityEnrichedList;
    }

    private static Timestamp getCurrentTimestamp() {
        Date date= new Date();
        long time = date.getTime();
        return new Timestamp(time);
    }
}
