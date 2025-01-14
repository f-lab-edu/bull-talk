package com.lima.consoleservice.domain.auth.model.response;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.lang.Nullable;

public record AuthResponse(

    @Schema(description = "성공 코드") String code,

    @Nullable
    @Schema(description = "결과 값")
    String result

) {
  public AuthResponse(String code) {
    this(code, null);
  }

}
