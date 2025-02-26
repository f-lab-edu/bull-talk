package com.lima.websocketservice.domain.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.websocketservice.domain.chat.model.ChatMessage;
import com.lima.websocketservice.domain.chat.service.FluentBitService;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
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
  private final RedisTemplate<String, String> redisTemplate; // 값 타입을 String으로 변경
  private final ObjectMapper objectMapper = new ObjectMapper();

  // 세션 ID와 WebSocketSession을 매핑하는 로컬 맵
  private final Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
  private static final String ROOM_KEY_PREFIX = "chat:room:";

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    int roomId = extractRoomId(session.getUri().getQuery());
    String roomKey = ROOM_KEY_PREFIX + roomId;

    // 세션 ID를 Redis에 저장
    redisTemplate.opsForSet().add(roomKey, session.getId());
    // 로컬 맵에 세션 객체 저장
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
      String roomKey = ROOM_KEY_PREFIX + roomId;
      Set<String> sessionIds = redisTemplate.opsForSet().members(roomKey);
      if (sessionIds != null) {
        for (String sessionId : sessionIds) {
          WebSocketSession webSocketSession = sessionMap.get(sessionId);
          if (webSocketSession != null && webSocketSession.isOpen()) {
            webSocketSession.sendMessage(new TextMessage(payload));
          }
        }
      }
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
      // 빈 방 제거까지.
      redisTemplate.delete(roomKey);
    }
    log.info("Session disconnected from room {}: {}", roomId, session.getId());
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