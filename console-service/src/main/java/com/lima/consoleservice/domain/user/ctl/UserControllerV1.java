package com.lima.consoleservice.domain.user.ctl;

import com.lima.consoleservice.domain.auth.model.request.UpdateUserRequest;
import com.lima.consoleservice.domain.auth.model.response.AuthResponse;
import com.lima.consoleservice.domain.user.svc.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "유저 API")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserControllerV1 {

  private final UserService userService;

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
