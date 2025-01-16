package com.lima.consoleservice.domain.auth.svc;

import com.lima.consoleservice.common.exception.BullTalkException;
import com.lima.consoleservice.common.exception.ErrorCode;
import com.lima.consoleservice.config.security.JwtTokenProvider;
import com.lima.consoleservice.domain.auth.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.auth.model.request.LoginUserRequest;
import com.lima.consoleservice.domain.auth.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.auth.model.response.AuthResponse;
import com.lima.consoleservice.domain.repository.UserRepository;
import com.lima.consoleservice.domain.repository.entity.User;
import com.lima.consoleservice.domain.repository.entity.UserCredentials;
import com.lima.consoleservice.config.Hasher;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate<String, Object> redisTemplate;
  private final UserRepository userRepository;
  private final Hasher hasher;

  @Transactional
  public AuthResponse createUser(@Valid CreateUserRequest request) {
    // email을 이용하여 유저가 존재하는지 확인.
    Optional<User> user = userRepository.findByEmail(request.email());

    if (user.isPresent()) {
      log.error("{}: {}", ErrorCode.USER_ALREADY_EXISTS, request.email());
      throw new BullTalkException(ErrorCode.USER_ALREADY_EXISTS);
    }

    // 없으면 새로 생성
    User newUser = this.newUser(request.email(), request.name());
    UserCredentials userCredentials = this.newUserCredentials(request.password(), newUser);
    newUser.setUserCredentials(userCredentials);

    userRepository.save(newUser);
    return new AuthResponse(ErrorCode.SUCCESS.getMessage(), request.email());
  }

  private UserCredentials newUserCredentials(@NotBlank @NotNull String password, User newUser) {
    return UserCredentials.builder()
        .user(newUser)
        .hashedPassword(hasher.getHashingValue(password))
        .build();
  }

  private User newUser(@NotBlank @NotNull String email, @NotBlank @NotNull String name) {
    return User.builder()
        .email(email)
        .name(name)
        .createDate(new Timestamp(System.currentTimeMillis()))
        .build();
  }

  public AuthResponse login(LoginUserRequest request) {
    Optional<User> user = userRepository.findByEmail(request.email());
    if (user.isEmpty()) {
      log.error("{}: {}", ErrorCode.NOT_EXIST_USER, request.email());
      throw new BullTalkException(ErrorCode.NOT_EXIST_USER);
    }
    user.map(u -> {
      String hashingValue = hasher.getHashingValue(request.password());

      // 비밀번호가 일치하지 않는다면.
      if (!u.getUserCredentials().getHashedPassword().equals(hashingValue)) {
        throw new BullTalkException(ErrorCode.MIS_MATCH_PASSWORD);
      }
      return hashingValue;
    });

    String token = jwtTokenProvider.createToken(request.email());
    // redis에 세션을 담아보쟈
    redisTemplate.opsForValue().set("session: " + request.email(), token, 30, TimeUnit.MINUTES);
    return new AuthResponse(ErrorCode.SUCCESS.getMessage(), token);
  }

  public String getCurrentUserEmail() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      return String.valueOf(authentication.getPrincipal());
    }
    return null; // 인증되지 않음
  }

  public AuthResponse logout(String email) {
    
    return null;
  }
}
