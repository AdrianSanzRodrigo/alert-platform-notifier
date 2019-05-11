package com.kschool.alertplatform.common.serdes;

import com.google.gson.reflect.TypeToken;
import com.kschool.alertplatform.common.model.AlertConfig;
import com.kschool.alertplatform.common.model.AlertConfigAggregated;
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



    private static JsonSerde createJsonSerde(Type typeToDeserialize) {
        return new JsonSerde<>(new JsonSerializer(JsonDeserializer.gson), new JsonDeserializer(typeToDeserialize));
    }

}
