package com.lima.websocketservice.domain.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.websocketservice.domain.chat.model.ChatMessage;
import com.lima.websocketservice.domain.chat.service.FluentBitService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private final FluentBitService fluentBitService;
  private final RedisTemplate<String, String> redisTemplate;
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final RedisMessageListenerContainer redisMessageListenerContainer;
  // 현재 연결된 모든 클라이언트의 WebSocket 세션 객체를 세션 ID와 매핑하여 저장
  private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
  private static final String ROOM_KEY_PREFIX = "chat:room:";

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    int roomId = extractRoomId(session.getUri().getQuery());
    String roomKey = ROOM_KEY_PREFIX + roomId;

    redisTemplate.opsForSet().add(roomKey, session.getId());
    sessionMap.put(session.getId(), session);

    log.info("New session connected to room {}: {}", roomId, session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    log.info("Received message: {}", payload);

    try {
      ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
      fluentBitService.sendToFluentBit(chatMessage);

      int roomId = chatMessage.getRoomId();
      String channel = ROOM_KEY_PREFIX + roomId;
      redisTemplate.convertAndSend(channel, payload); // Redis 채널에 메시지 게시
    } catch (Exception e) {
      log.error("Failed to process message", e);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    int roomId = extractRoomId(session.getUri().getQuery());
    String roomKey = ROOM_KEY_PREFIX + roomId;

    redisTemplate.opsForSet().remove(roomKey, session.getId());
    sessionMap.remove(session.getId());

    Long remainingSessions = redisTemplate.opsForSet().size(roomKey);
    if (remainingSessions != null && remainingSessions == 0) {
      redisTemplate.delete(roomKey);
      log.info("Room {} removed as no sessions remain", roomId);
    }
    log.info("Session disconnected from room {}: {}", roomId, session.getId());
  }

  @PostConstruct
  public void init() {
    redisMessageListenerContainer.addMessageListener((message, pattern) -> {
      String payload = new String(message.getBody());
      String channel = new String(message.getChannel());
      int roomId = Integer.parseInt(channel.split(":")[2]);
      String roomKey = ROOM_KEY_PREFIX + roomId;

      Set<String> sessionIds = redisTemplate.opsForSet().members(roomKey);
      if (sessionIds != null) {
        for (String sessionId : sessionIds) {
          WebSocketSession ws = sessionMap.get(sessionId);
          if (ws != null && ws.isOpen()) {
            try {
              ws.sendMessage(new TextMessage(payload));
            } catch (IOException e) {
              log.error("Failed to send message to session {}: {}", sessionId, e.getMessage());
            }
          }
        }
      }
    }, new ChannelTopic(ROOM_KEY_PREFIX + "*"));
  }

  private int extractRoomId(String query) {
    if (query != null && query.startsWith("roomId=")) {
      try {
        return Integer.parseInt(query.substring("roomId=".length()));
      } catch (NumberFormatException e) {
        log.error("Invalid roomId in query: {}", query);
      }
    }
    return 0;
  }
}