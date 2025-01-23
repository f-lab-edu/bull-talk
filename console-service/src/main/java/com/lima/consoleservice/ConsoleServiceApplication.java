package com.lima.consoleservice;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// http://localhost:9001/swagger-ui.html

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Bull-Talk", version = "v1", description = "Bull-Talk API"))
public class ConsoleServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(ConsoleServiceApplication.class, args);
  }

}
