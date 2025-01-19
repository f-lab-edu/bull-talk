package com.lima.consoleservice.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Login 요청")
public record LoginUserRequest(

    @Schema(description = "유저 이메일")
    @NotBlank
    @NotNull
    String email,

    @Schema(description = "유저 비밀번호")
    @NotBlank
    @NotNull
    String password
) {}
