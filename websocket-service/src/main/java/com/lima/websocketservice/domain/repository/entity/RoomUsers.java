package com.lima.websocketservice.domain.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "room_users")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoomUsers {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "room_id", nullable = false)
  private ChatRooms chatRoom;

  @Column(name = "user_id", nullable = false)
  private Long userId;

  @Column(name = "joined_at", nullable = false)
  private LocalDateTime joinedAt;

}
