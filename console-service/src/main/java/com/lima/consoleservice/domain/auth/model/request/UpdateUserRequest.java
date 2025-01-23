package com.lima.consoleservice.domain.auth.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(

    @NotBlank
    @NotNull
    String name

) {}
