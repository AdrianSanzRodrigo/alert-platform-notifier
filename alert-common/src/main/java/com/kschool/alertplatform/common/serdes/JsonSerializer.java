package com.kschool.alertplatform.common.serdes;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Map;


public class JsonSerializer implements Serializer<Object> {

    StringSerializer stringSerializer = new StringSerializer();

    private Gson objectToJsonConverter;

    public JsonSerializer() {
        this.objectToJsonConverter = JsonDeserializer.gson;
    }

    public JsonSerializer(Gson objectToJsonConverter) {
        this.objectToJsonConverter = objectToJsonConverter;
    }

    public void configure(Map<String, ?> var1, boolean var2) {}

    public byte[] serialize(String topic, Object data) {
        String jsonObject = objectToJsonConverter.toJson(data);
        return stringSerializer.serialize(topic, jsonObject);
    }

    public void close() {}
}