package com.lima.consoleservice.domain.auth.ctl;

import com.lima.consoleservice.domain.auth.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.auth.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.auth.model.response.AuthResponse;
import com.lima.consoleservice.domain.auth.svc.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth API", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthControllerV1 {

  private final AuthService authService;

  @Operation(
      summary = "새로운 유저를 생성합니다.",
      description = "새로운 유저 생성"
  )
  @PostMapping("/signup")
  public AuthResponse createUser(@RequestBody @Valid CreateUserRequest request) {
    return authService.createUser(request);
  }

  @Operation(
      summary = "존재하는 유저의 정보를 업데이트 한다.",
      description = "존재하는 유저의 정보를 업데이트 한다."
  )
  @PutMapping("/users/{userId}")
  public AuthResponse updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserRequest request) {
    return authService.updateUser(userId, request);
  }

  @DeleteMapping("/users/{userId}")
  public AuthResponse deleteUser(@PathVariable Long userId) {
    return authService.deleteUser(userId);
  }
}
