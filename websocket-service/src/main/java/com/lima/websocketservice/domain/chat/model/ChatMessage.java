package com.lima.websocketservice.domain.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

  private String sender;
  private String receiver;
  private String message;
  private int roomId;
  private long timestamp;
}
