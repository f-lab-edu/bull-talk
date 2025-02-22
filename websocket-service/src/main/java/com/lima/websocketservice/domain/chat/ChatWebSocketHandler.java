package com.lima.websocketservice.domain.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.websocketservice.domain.chat.model.ChatMessage;
import com.lima.websocketservice.domain.chat.service.FluentBitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private final FluentBitService fluentBitService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  // roomId별 세션 관리
  private final Map<Integer, Set<WebSocketSession>> roomSessions = Collections.synchronizedMap(new HashMap<>());

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    super.afterConnectionEstablished(session);
    // 쿼리 파라미터에서 roomId 추출
    String query = session.getUri().getQuery();
    int roomId = extractRoomId(query);

    // roomId에 해당하는 세션 집합이 없으면 생성
    roomSessions.computeIfAbsent(roomId, k -> Collections.synchronizedSet(new HashSet<>())).add(session);
    log.info("New session connected to room {}: {}", roomId, session.getId());
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    String payload = message.getPayload();
    log.info("Received message: {}", payload);

    try {
      ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
      fluentBitService.sendToFluentBit(chatMessage);

      // 해당 roomId의 세션에만 메시지 전송
      int roomId = chatMessage.getRoomId();
      Set<WebSocketSession> sessionsInRoom = roomSessions.getOrDefault(roomId, Collections.emptySet());
      for (WebSocketSession webSocketSession : sessionsInRoom) {
        if (webSocketSession.isOpen()) {
          webSocketSession.sendMessage(new TextMessage(payload));
        }
      }
    } catch (Exception e) {
      log.error("Failed to process message", e);
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    String query = session.getUri().getQuery();
    int roomId = extractRoomId(query);
    Set<WebSocketSession> sessionsInRoom = roomSessions.get(roomId);
    if (sessionsInRoom != null) {
      sessionsInRoom.remove(session);
      if (sessionsInRoom.isEmpty()) {
        roomSessions.remove(roomId); // 빈 방 제거
      }
    }
    log.info("Session disconnected from room {}: {}", roomId, session.getId());
  }

  // 쿼리에서 roomId 추출
  private int extractRoomId(String query) {
    if (query != null && query.startsWith("roomId=")) {
      try {
        return Integer.parseInt(query.substring("roomId=".length()));
      } catch (NumberFormatException e) {
        log.error("Invalid roomId in query: {}", query);
      }
    }
    return 0; // 기본값 (필요 시 예외 처리로 변경 가능)
  }
}