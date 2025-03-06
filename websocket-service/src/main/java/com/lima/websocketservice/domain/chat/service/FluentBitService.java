package com.lima.websocketservice.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.websocketservice.domain.chat.model.ChatData;
import com.lima.websocketservice.domain.chat.model.ChatMessage;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class FluentBitService {

  @Value("${fluent-bit.url}")
  private String fluentBitUrl;

  @Value("${fluent-bit.username}")
  private String elasticsearchUsername;

  @Value("${fluent-bit.password}")
  private String elasticsearchPassword;

  private final RestTemplate restTemplate;
  private final ObjectMapper objectMapper;

  public FluentBitService(RestTemplate restTemplate, ObjectMapper objectMapper) {
    this.restTemplate = restTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendToFluentBit(ChatMessage chatMessage) {
    try {
      ChatData chatData = getChatData(chatMessage);

      String requestJson = objectMapper.writeValueAsString(chatData);

      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_JSON);
      String auth = elasticsearchUsername + ":" + elasticsearchPassword;
      String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
      headers.add("Authorization", "Basic " + encodedAuth);

      HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
      ResponseEntity<String> response = restTemplate.postForEntity(fluentBitUrl, entity, String.class);

      if (response.getStatusCode().is2xxSuccessful()) {
        log.info("Fluent Bit에 메시지 잘 보냄: {}, 상태 코드: {}, 응답: {}", requestJson, response.getStatusCode(), response.getBody());
      } else {
        log.warn("Fluent Bit에 보내기 실패, 상태: {}, 응답: {}", response.getStatusCode(), response.getBody());
      }
    } catch (Exception e) {
      log.error("Fluent Bit에 메시지 보내다가 문제 생김", e);
    }
  }

  private static ChatData getChatData(ChatMessage chatMessage) {
    ChatData chatData = new ChatData();
    chatData.setSender(chatMessage.getSender());
    chatData.setReceiver(chatMessage.getReceiver());
    chatData.setMessage(chatMessage.getMessage());
    chatData.setRoomId(String.valueOf(chatMessage.getRoomId()));
    chatData.setCreatedAt(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        .format(new Date(chatMessage.getTimestamp())));
    chatData.setDeleted(false);
    chatData.setMessageId(UUID.randomUUID().toString());
    chatData.setStatus("sent");
    chatData.setMessageType("text");
    chatData.setSenderUserId(chatMessage.getSender());
    chatData.setReceiverUserId(chatMessage.getReceiver());
    chatData.setRoomName("Room-" + chatMessage.getRoomId());
    chatData.setEditedAt(null);
    return chatData;
  }
}