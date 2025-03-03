package com.lima.websocketservice.domain.chat.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChatData {

  @JsonProperty("created_at")
  private String createdAt;

  @JsonProperty("edited_at")
  private String editedAt;

  @JsonProperty("is_deleted")
  private boolean isDeleted;

  @JsonProperty("message")
  private String message;

  @JsonProperty("message_id")
  private String messageId;

  @JsonProperty("message_type")
  private String messageType;

  @JsonProperty("receiver")
  private String receiver;

  @JsonProperty("receiver_user_id")
  private String receiverUserId;

  @JsonProperty("room_name")
  private String roomName;

  @JsonProperty("room_id")
  private String roomId;

  @JsonProperty("sender")
  private String sender;

  @JsonProperty("sender_user_id")
  private String senderUserId;

  @JsonProperty("status")
  private String status;
}