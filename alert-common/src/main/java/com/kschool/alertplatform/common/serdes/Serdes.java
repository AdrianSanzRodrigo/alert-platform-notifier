package com.kschool.alertplatform.common.serdes;

import com.google.gson.reflect.TypeToken;
import com.kschool.alertplatform.common.model.*;
import com.kschool.alertplatform.common.model.airquality.AirQualityEnriched;
import com.kschool.alertplatform.common.model.airquality.AirQualityRaw;
import com.kschool.alertplatform.common.model.alert.Alert;
import com.kschool.alertplatform.common.model.alert.AlertConfig;
import com.kschool.alertplatform.common.model.alert.AlertConfigAggregated;
import com.kschool.alertplatform.common.model.trafficdensity.TrafficDensityEnriched;
import com.kschool.alertplatform.common.model.trafficdensity.TrafficDensityRaw;
import com.kschool.alertplatform.common.model.weather.WeatherEnriched;
import com.kschool.alertplatform.common.model.weather.WeatherRaw;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class Serdes {

    public static final Consumed<String, AlertConfig> alertsConfigConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(), createJsonSerde(AlertConfig.class));

    public static final Produced<String, List<AlertConfigAggregated>> alertsConfigAggregatedProducer =
            Produced.with(org.apache.kafka.common.serialization.Serdes.String(),
                    createJsonSerde(AlertConfigAggregated.class));

    public static Materialized<String, Map<String, AlertConfig>, KeyValueStore<Bytes, byte[]>> tableMaterialization =
            Materialized.<String, Map<String, AlertConfig>, KeyValueStore<Bytes, byte[]>>as("aggregations")
                    .withKeySerde(org.apache.kafka.common.serialization.Serdes.String())
                    .withValueSerde(createJsonSerde(new TypeToken<Map<String, AlertConfig>>() {
                    }.getType()));

    public static Materialized<String, List<AlertConfigAggregated>, KeyValueStore<Bytes, byte[]>> alertsConfigMaterialization =
            Materialized.<String, Map<String, AlertConfig>, KeyValueStore<Bytes, byte[]>>as("configaggregations")
                    .withKeySerde(org.apache.kafka.common.serialization.Serdes.String())
                    .withValueSerde(createJsonSerde(new TypeToken<List<AlertConfigAggregated>>() {
                    }.getType()));

    public static final Consumed<String, List<AlertConfigAggregated>> alertsConfigAggregatedConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(),new JsonSerde<>(new TypeToken<List<AlertConfigAggregated>>(){}.getType()));

    public static final Produced<String, Alert> alertProducer =
            Produced.with(org.apache.kafka.common.serialization.Serdes.String(),
                    createJsonSerde(Alert.class));

    public static final Consumed<String, AirQualityRaw> airQualityRawConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(), createJsonSerde(AirQualityRaw.class));

    public static final Produced<String, AirQualityEnriched> airQualityEnrichedProducer =
            Produced.with(org.apache.kafka.common.serialization.Serdes.String(),
                    createJsonSerde(AirQualityEnriched.class));

    public static final Consumed<String, AirQualityEnriched> airQualityEnrichedConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(), createJsonSerde(AirQualityEnriched.class));

    public static final Consumed<String, WeatherRaw> weatherRawConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(), createJsonSerde(WeatherRaw.class));

    public static final Produced<String, WeatherEnriched> weatherEnrichedProducer =
            Produced.with(org.apache.kafka.common.serialization.Serdes.String(),
                    createJsonSerde(WeatherEnriched.class));

    public static final Consumed<String, WeatherEnriched> weatherEnrichedConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(), createJsonSerde(WeatherEnriched.class));

    public static final Consumed<String, TrafficDensityRaw> trafficDensityRawConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(), createJsonSerde(TrafficDensityRaw.class));

    public static final Produced<String, TrafficDensityEnriched> trafficDensityEnrichedProducer =
            Produced.with(org.apache.kafka.common.serialization.Serdes.String(),
                    createJsonSerde(TrafficDensityEnriched.class));

    public static final Consumed<String, TrafficDensityEnriched> trafficDensityEnrichedConsumer =
            Consumed.with(org.apache.kafka.common.serialization.Serdes.String(), createJsonSerde(TrafficDensityEnriched.class));

    public static final Produced<String, EnrichedEvents> eventsEnrichedProducer =
            Produced.with(org.apache.kafka.common.serialization.Serdes.String(),
                    createJsonSerde(EnrichedEvents.class));

    public static final Produced<String, List<EnrichedEvents>> eventsEnrichedListProducer =
            Produced.with(org.apache.kafka.common.serialization.Serdes.String(),
                    createJsonSerde(EnrichedEvents.class));

    private static JsonSerde createJsonSerde(Type typeToDeserialize) {
        return new JsonSerde<>(new JsonSerializer(JsonDeserializer.gson), new JsonDeserializer(typeToDeserialize));
    }

}
