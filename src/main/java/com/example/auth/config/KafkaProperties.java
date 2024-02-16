package com.example.auth.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProperties {
    private String bootStrapServers = "bootstrap-servers";
    private Map<String, String> consumer = new HashMap<>();

    public String getBootStrapServers() {
        return bootStrapServers;
    }

    public Map<String, Object> getConsumerProps() {
        Map<String, Object> properties = new HashMap<>(this.consumer);
        if(!properties.containsKey("bootstrap.servers")) {
            properties.put("bootstrap.servers", this.bootStrapServers);
        }
        properties.put("key.deserializer", this.consumer.get("key.deserializer"));
        properties.put("value.deserializer", this.consumer.get("value.deserializer"));
        properties.put("group.id", this.consumer.get("group.id"));
        properties.put("auto.offset.reset", this.consumer.get("auto.offset.reset"));
        return properties;
    }
}
