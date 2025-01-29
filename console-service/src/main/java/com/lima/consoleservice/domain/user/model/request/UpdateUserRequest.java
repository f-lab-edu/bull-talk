package com.lima.consoleservice.domain.user.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "유저 정보를 업데이트 한다.")
public record UpdateUserRequest(

    @NotBlank
    @NotNull
    String name

) {}
