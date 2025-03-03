package com.lima.websocketservice.domain.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lima.websocketservice.domain.chat.model.ChatMessage;
import com.lima.websocketservice.domain.chat.service.FluentBitService;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@SpringBootTest
class ChatWebSocketHandlerTest {

  @Mock
  private FluentBitService fluentBitService;

  @Mock
  private RedisTemplate<String, String> redisTemplate;

  @Mock
  private SetOperations<String, String> setOperations;

  @Mock
  private WebSocketSession webSocketSession;

  @InjectMocks
  private ChatWebSocketHandler chatWebSocketHandler;

  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    objectMapper = new ObjectMapper();
    when(redisTemplate.opsForSet()).thenReturn(setOperations);
  }

  @Test
  void testAfterConnectionEstablished_Success() throws Exception {
    // Arrange
    URI uri = new URI("ws://localhost:8084/chat?roomId=1");
    when(webSocketSession.getUri()).thenReturn(uri);
    when(webSocketSession.getId()).thenReturn("session123");

    // Act
    chatWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Assert
    verify(setOperations).add("chat:room:1", "session123");
    verify(webSocketSession, times(1)).getId();
  }

  @Test
  void testHandleTextMessage_ValidMessage() throws Exception {
    // Arrange
    ChatMessage chatMessage = new ChatMessage("user1", "user2", "Hello", 1, System.currentTimeMillis());
    String payload = objectMapper.writeValueAsString(chatMessage);
    TextMessage textMessage = new TextMessage(payload);

    // Act
    chatWebSocketHandler.handleTextMessage(webSocketSession, textMessage);

    // Assert
    verify(fluentBitService).sendToFluentBit(chatMessage);
    verify(redisTemplate).convertAndSend("chat:room:1", payload);
  }

  @Test
  void testHandleTextMessage_InvalidPayload() throws Exception {
    // Arrange
    TextMessage textMessage = new TextMessage("invalid json");

    // Act
    chatWebSocketHandler.handleTextMessage(webSocketSession, textMessage);

    // Assert
    verify(fluentBitService, never()).sendToFluentBit(any());
    verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
  }

  @Test
  void testAfterConnectionClosed_SessionRemoved() throws Exception {
    // Arrange
    URI uri = new URI("ws://localhost:8084/chat?roomId=2");
    when(webSocketSession.getUri()).thenReturn(uri);
    when(webSocketSession.getId()).thenReturn("session456");
    when(setOperations.size("chat:room:2")).thenReturn(0L);

    chatWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Act
    chatWebSocketHandler.afterConnectionClosed(webSocketSession, CloseStatus.NORMAL);

    // Assert
    verify(setOperations).remove("chat:room:2", "session456");
    verify(redisTemplate).delete("chat:room:2");
    verify(setOperations).size("chat:room:2");
  }

  @Test
  void testAfterConnectionClosed_RoomNotEmpty() throws Exception {
    // Arrange
    URI uri = new URI("ws://localhost:8084/chat?roomId=3");
    when(webSocketSession.getUri()).thenReturn(uri);
    when(webSocketSession.getId()).thenReturn("session789");
    when(setOperations.size("chat:room:3")).thenReturn(1L);

    chatWebSocketHandler.afterConnectionEstablished(webSocketSession);

    // Act
    chatWebSocketHandler.afterConnectionClosed(webSocketSession, CloseStatus.NORMAL);

    // Assert
    verify(setOperations).remove("chat:room:3", "session789");
    verify(redisTemplate, never()).delete(anyString());
  }
}