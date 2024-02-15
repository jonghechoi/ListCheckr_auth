package com.example.auth.adaptor;

import com.example.auth.config.KafkaProperties;
import com.example.auth.domain.User;
import com.example.auth.domain.dto.MemberChanged;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
public class MemberManagementConsumer {
    private final AtomicBoolean closed = new AtomicBoolean(false);

    public static final String TOPIC = "topic_member";
    private final KafkaProperties kafkaProperties;
    private KafkaConsumer<String, String> kafkaConsumer;
    private AuthService authService;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public MemberManagementConsumer(KafkaProperties kafkaProperties, AuthService authService) {
        this.kafkaProperties = kafkaProperties;
        this.authService = authService;
    }

    @PostConstruct
    public void start() {
        log.info("Kafka Consumer starting...");
        this.kafkaConsumer = new KafkaConsumer<>(kafkaProperties.getConsumerProps());
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
        kafkaConsumer.subscribe(Collections.singleton(TOPIC));
        log.info("Kafka Consumer started...");

        executorService.execute(() -> {
            try {
                while (!closed.get()) {
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(3));

                    for(ConsumerRecord<String, String> record : records) {
                        log.info("Consumed message in {} : {}", TOPIC, record.value());
                        ObjectMapper objectMapper = new ObjectMapper();
                        MemberChanged memberChanged = objectMapper.readValue(record.value(), MemberChanged.class);

                        // member 정보를 user로 매핑한 후 repository에서 저장할 수 있게 처리
                        authService.save(UserRequestDtoFromKfaka.kafkaDataToUser(memberChanged));
                    }
                }
                kafkaConsumer.commitSync();
            } catch (WakeupException e) {
                if(!closed.get()) {
                    throw e;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                log.info("kafka consumer close");
                kafkaConsumer.close();
            }
        });
    }

    @PreDestroy
    public void shutdown() {
        log.info("Shutdown kafka consumer");
        closed.set(true);
        kafkaConsumer.wakeup();
    }
}
