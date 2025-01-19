package com.lima.consoleservice.domain.user.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(

    @NotBlank
    @NotNull
    String name

) {}
