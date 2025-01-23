package com.lima.consoleservice.domain.user.controller;

import com.lima.consoleservice.domain.user.model.request.CreateUserRequest;
import com.lima.consoleservice.domain.user.model.request.LoginUserRequest;
import com.lima.consoleservice.domain.user.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.user.model.response.AuthResponse;
import com.lima.consoleservice.domain.user.service.UserService;
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
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserControllerV1 {

  private final UserService userService;

  @Operation(
      summary = "유저 생성.",
      description = "새로운 유저를 생성 합니다"
  )
  @PostMapping("/signup")
  public AuthResponse createUser(@RequestBody @Valid CreateUserRequest request) {
    return userService.createUser(request);
  }

  @Operation(
      summary = "로그인 처리",
      description = "로그인을 진행 한다.."
  )
  @GetMapping("/login")
  public AuthResponse login(LoginUserRequest request) {
    return userService.login(request);
  }

  @Operation(
      summary = "로그아웃 처리",
      description = "로그아웃을 진행 한다.."
  )
  @GetMapping("/logout")
  public AuthResponse logout(Long userId) {
    return userService.logout(userId);
  }

  @Operation(
      summary = "유저의 정보 업데이트",
      description = "존재하는 유저의 정보를 업데이트 한다."
  )
  @PutMapping("/{userId}")
  public AuthResponse updateUser(@PathVariable Long userId, @RequestBody @Valid UpdateUserRequest request) {
    return userService.updateUser(userId, request);
  }

  @Operation(
      summary = "유저의 정보 삭제",
      description = "존재하는 유저의 정보를 삭제 한다."
  )
  @DeleteMapping("/{userId}")
  public AuthResponse deleteUser(@PathVariable Long userId) {
    return userService.deleteUser(userId);
  }
}

