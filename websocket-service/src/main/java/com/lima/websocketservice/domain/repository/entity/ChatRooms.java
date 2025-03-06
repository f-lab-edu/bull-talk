package com.lima.websocketservice.domain.repository.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;

@Entity
@Data
@Table(name = "chat_rooms")
public class ChatRooms {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; // 채팅방 ID

  @Column(name = "room_name", nullable = false)
  private String roomName; // 채팅방 이름

  @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<RoomUsers> roomUsers = new HashSet<>(); // 채팅방에 접속한 사용자들

}
