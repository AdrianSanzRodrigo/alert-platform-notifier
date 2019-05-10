package com.kschool.alertstreaming.utils;

import com.google.gson.reflect.TypeToken;
import com.kschool.alertstreaming.model.AlertConfig;
import com.kschool.alertstreaming.model.AlertConfigAggregated;
import com.kschool.alertstreaming.serdes.JsonDeserializer;
import com.kschool.alertstreaming.serdes.JsonSerde;
import com.kschool.alertstreaming.serdes.JsonSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.kschool.alertstreaming.serdes.JsonDeserializer.gson;

public class KafkaUtils {

    public static final Consumed<String, AlertConfig> alertsConfigConsumer =
            Consumed.with(Serdes.String(), createJsonSerde(AlertConfig.class));

    public static final Produced<String, List<AlertConfigAggregated>> alertsConfigAggregatedProducer =
            Produced.with(Serdes.String(),
                    createJsonSerde(AlertConfigAggregated.class));

    public static Materialized<String, Map<String, AlertConfig>, KeyValueStore<Bytes, byte[]>> tableMaterialization =
            Materialized.<String, Map<String, AlertConfig>, KeyValueStore<Bytes, byte[]>>as("aggregations")
                    .withKeySerde(Serdes.String())
                    .withValueSerde(createJsonSerde(new TypeToken<Map<String, AlertConfig>>() {
                    }.getType()));

    private static JsonSerde createJsonSerde(Type typeToDeserialize) {
        return new JsonSerde<>(new JsonSerializer(gson), new JsonDeserializer(typeToDeserialize));
    }

    public static void modifyCustomAlerts(
            Map<String, AlertConfig> userAlerts, AlertConfig modification
    ) {
        if(Objects.equals(modification.getAction().toLowerCase(), "delete")
                && userAlerts.containsKey(modification.getId()))
            userAlerts.remove(modification.getId());

        else if(Objects.equals(modification.getAction().toLowerCase(), "update")
                && userAlerts.containsKey(modification.getId()))
            userAlerts.replace(modification.getId(), modification);

        else if(Objects.equals(modification.getAction().toLowerCase(), "create")
                && !userAlerts.containsKey(modification.getId()))
            userAlerts.putIfAbsent(modification.getId(), modification);

        else if(Objects.equals(modification.getAction().toLowerCase(), "delete_all"))
            userAlerts.clear();

        else throw new IllegalStateException("Invalid operation: " + modification.getAction());
    }
}
