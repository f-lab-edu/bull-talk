package com.lima.websocketservice.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.websocketservice.domain.chat.model.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FluentBitService {

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;
  private final String fluentBitUrl = "http://192.168.16.6:9880";

  public FluentBitService(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendToFluentBit(ChatMessage chatMessage) {
    try {
      String message = objectMapper.writeValueAsString(chatMessage);
      restTemplate.postForEntity(fluentBitUrl, message, String.class);
      log.info("Message sent to Fluent Bit: {}", message);
    } catch (Exception e) {
      log.error("Failed to send message to Fluent Bit", e);
    }
  }
}