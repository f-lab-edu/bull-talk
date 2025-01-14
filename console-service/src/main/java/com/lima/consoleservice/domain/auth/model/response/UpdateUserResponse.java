package com.lima.consoleservice.domain.auth.model.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserResponse(

    @NotBlank
    @NotNull
    String name

) {}
