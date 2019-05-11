package com.kschool.alertplatform.common.serdes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kschool.alertplatform.common.utils.AlertLogger;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonDeserializer<T> implements Deserializer<T> {

    public static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss.S").create();
    private AlertLogger logger = new AlertLogger(JsonDeserializer.class.getName());
    private Type typeToDeserialize;
    private StringDeserializer stringDeserializer = new StringDeserializer();
    private Gson jsonDeserializer = gson;

    public JsonDeserializer(Type typeToDeserialize) {
        this.typeToDeserialize = typeToDeserialize;
    }

    public JsonDeserializer(Type typeToDeserialize, Gson jsonDeserializer) {
        this.typeToDeserialize = typeToDeserialize;
        this.jsonDeserializer = jsonDeserializer;
    }

    @Override
    public void configure(Map configs, boolean isKey) { }

    @Override
    public T deserialize(String topic, byte[] data) {
        String json = null;
        try {
            json = stringDeserializer.deserialize(topic, data);
            return jsonDeserializer.fromJson(json, typeToDeserialize);
        } catch (Exception e) {
            logger.error("Error deserializing: " + json, e);
            return null;
        }
    }

    @Override
    public void close() { }
}