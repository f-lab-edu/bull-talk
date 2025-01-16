package com.lima.consoleservice.domain.auth.ctl;

import com.lima.consoleservice.domain.auth.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.auth.model.request.LoginUserRequest;
import com.lima.consoleservice.domain.auth.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.auth.model.response.AuthResponse;
import com.lima.consoleservice.domain.auth.svc.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
      summary = "유저 생성.",
      description = "새로운 유저를 생성 합니다"
  )
  @PostMapping("/signup")
  public AuthResponse createUser(@RequestBody @Valid CreateUserRequest request) {
    return authService.createUser(request);
  }

  @Operation(
      summary = "로그인 처리",
      description = "로그인을 진행 한다.."
  )
  @GetMapping("/login")
  public AuthResponse login(LoginUserRequest request) {
    return authService.login(request);
  }

  @Operation(
      summary = "로그아웃 처리",
      description = "로그아웃을 진행 한다.."
  )
  @GetMapping("/logout")
  public AuthResponse logout(String email) {
    return authService.logout(email);
  }
}
