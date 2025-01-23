package com.lima.consoleservice.domain.auth.service;

import com.lima.consoleservice.common.constants.ResponseConstants;
import com.lima.consoleservice.domain.auth.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.auth.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.auth.model.response.AuthResponse;
import com.lima.consoleservice.domain.repository.UserRepository;
import com.lima.consoleservice.domain.repository.entity.User;
import com.lima.consoleservice.domain.repository.entity.UserCredentials;
import com.lima.consoleservice.utils.security.Hasher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;

  public Hasher sha256Hasher() {
    return new Hasher("SHA-256");
  }

  @Transactional
  public AuthResponse createUser(@Valid CreateUserRequest request) {
    // email을 이용하여 유저가 존재하는지 확인.
    Optional<User> user = userRepository.findByEmail(request.email());

    if (user.isPresent()) {
      log.error("USER_ALREADY_EXISTS: {}", request.email());
      // 커스텀 Exception을 사용해봐야겠다.
      throw new RuntimeException("USER_ALREADY_EXISTS");
    }

    // 없으면 새로 생성
    User newUser = this.newUser(request.email(), request.name());
    UserCredentials userCredentials = this.newUserCredentials(request.password(), newUser);
    newUser.setUserCredentials(userCredentials);

    userRepository.save(newUser);
    return new AuthResponse(ResponseConstants.SUCCESS, request.email());
  }

  private UserCredentials newUserCredentials(@NotBlank @NotNull String password, User newUser) {
    return UserCredentials.builder()
        .user(newUser)
        .hashedPassword(sha256Hasher().getHashingValue(password))
        .build();
  }

  private User newUser(@NotBlank @NotNull String email, @NotBlank @NotNull String name) {
    return User.builder()
        .email(email)
        .name(name)
        .createDate(new Timestamp(System.currentTimeMillis()))
        .build();
  }

  @Transactional
  public AuthResponse updateUser(Long userId, @Valid UpdateUserRequest request) {
    // 유저가 존재하는지 확인
    User user = getUser(userId);
    user.setName(request.name());
    // 유저 업데이트
    userRepository.save(user);
    return new AuthResponse(ResponseConstants.SUCCESS, user.getName());
  }

  public AuthResponse deleteUser(Long userId) {
    getUser(userId);
    userRepository.deleteById(userId);
    return new AuthResponse(ResponseConstants.SUCCESS);
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));
  }
}
