package com.budget.b.lite.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(String topic, Object event) {
        kafkaTemplate.send(topic, event);
    }
}
