package com.lima.consoleservice.domain.auth.model.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "유저 생성 response")
public record CreateUserResponse(
    @Schema(description = "유저 ID") String code

) {}
