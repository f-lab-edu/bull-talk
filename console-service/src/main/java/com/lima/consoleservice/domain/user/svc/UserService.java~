package com.lima.consoleservice.domain.user.svc;

import com.lima.consoleservice.common.exception.BullTalkException;
import com.lima.consoleservice.common.exception.ErrorCode;
import com.lima.consoleservice.config.Hasher;
import com.lima.consoleservice.config.security.JwtTokenProvider;
import com.lima.consoleservice.domain.auth.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.auth.model.request.LoginUserRequest;
import com.lima.consoleservice.domain.auth.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.auth.model.response.AuthResponse;
import com.lima.consoleservice.domain.auth.svc.AuthService;
import com.lima.consoleservice.domain.repository.UserRepository;
import com.lima.consoleservice.domain.repository.entity.User;
import com.lima.consoleservice.domain.repository.entity.UserCredentials;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final AuthService authService;

  @Transactional
  public AuthResponse updateUser(Long userId, @Valid UpdateUserRequest request) {
    User user = updateLoginUser(userId);

    user.setName(request.name());
    // 유저 업데이트
    userRepository.save(user);
    return new AuthResponse(ErrorCode.SUCCESS.getMessage(), user.getName());
  }

  private User updateLoginUser(Long userId) {
    User user = getUser(userId); // 수정하려는 사용자 정보가 현재 로그인한 사용자인지 확인
    String currentUserEmail = authService.getCurrentUserEmail(); // 현재 로그인 한 사용자

    if (!user.getEmail().equals(currentUserEmail)) {
      log.error("{}: {}", ErrorCode.NOT_MATCH_USER, currentUserEmail);
      throw new BullTalkException(ErrorCode.NOT_MATCH_USER);
    }
    return user;
  }

  public AuthResponse deleteUser(Long userId) {
    updateLoginUser(userId);
    userRepository.deleteById(userId);
    return new AuthResponse(ErrorCode.SUCCESS.getMessage());
  }

  private User getUser(Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new BullTalkException(ErrorCode.NOT_EXIST_USER));
  }

}
