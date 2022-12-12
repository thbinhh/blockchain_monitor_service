package com.example.blockchainmonitorservice.service.producer;

import com.example.blockchainmonitorservice.dto.kafkamessage.Transaction;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Producer {
    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public String sendMessage(Transaction transaction, String topic) throws JsonProcessingException {
        String transactionAsMessage = objectMapper.writeValueAsString(transaction);
        kafkaTemplate.send(topic,transactionAsMessage);

        log.info(topic + " transaction produced {}", transactionAsMessage);

        return "message sent";
    }
}
