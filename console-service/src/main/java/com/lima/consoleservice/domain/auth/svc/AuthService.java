package com.lima.consoleservice.domain.auth.svc;

import com.lima.consoleservice.domain.auth.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.auth.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.auth.model.response.CreateUserResponse;
import com.lima.consoleservice.domain.auth.model.response.UpdateUserResponse;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final Hasher hasher;


  public CreateUserResponse createUser(@Valid CreateUserRequest request) {
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

    User saveUser = userRepository.save(newUser);

    if (saveUser == null) {
      log.error("CREATE_USER_FAILED: {}", request.email());
      // 커스텀 Exception을 사용해봐야겠다.
      throw new RuntimeException("CREATE_USER_FAILED");
    }

    return new CreateUserResponse(request.email());
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

  public UpdateUserResponse updateUser(Long userId, @Valid UpdateUserRequest request) {
    // 유저가 존재하는지 확인

    // 유저 업데이트
    return null;
  }
}
