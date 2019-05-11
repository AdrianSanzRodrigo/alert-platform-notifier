package com.kschool.alertplatform.common.serdes;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.lang.reflect.Type;
import java.util.Map;

public class JsonSerde<T> implements Serde<T> {
    private JsonSerializer serializer;
    private JsonDeserializer<T> deserializer;

    public JsonSerde(Type tClass) {
        serializer = new JsonSerializer();
        deserializer = new JsonDeserializer<>(tClass);
    }

    public JsonSerde(JsonSerializer serializer, JsonDeserializer deserializer) {
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public void configure(Map configs, boolean isKey) {
        this.serializer.configure(configs, isKey);
        this.deserializer.configure(configs, isKey);
    }

    @Override
    public void close() {
        this.serializer.close();
        this.deserializer.close();
    }

    @Override
    public Serializer serializer() {
        return this.serializer;
    }

    @Override
    public Deserializer deserializer() {
        return this.deserializer;
    }
}