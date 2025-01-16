package com.lima.consoleservice.domain.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_user_credentials")
public class UserCredentials implements Serializable {

  private static final long serialVersionUID = -2563473576099666594L;

  @Id
  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "hashed_password")
  private String hashedPassword;

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserCredentials that = (UserCredentials) o;
    return Objects.equals(user, that.user) && Objects.equals(hashedPassword,
        that.hashedPassword);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, hashedPassword);
  }
}
