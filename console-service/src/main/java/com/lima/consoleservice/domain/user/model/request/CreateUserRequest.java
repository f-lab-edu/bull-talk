package com.lima.consoleservice.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유저를 생성 한다.")
public record CreateUserRequest(

    @Schema(description = "유저 이메일")
    @NotBlank
    @NotNull
    String email,

    @Schema(description = "유저 이름")
    @NotBlank
    @NotNull
    String name,

    @Schema(description = "유저 비밀번호")
    @NotBlank
    @NotNull
    String password
) {}
