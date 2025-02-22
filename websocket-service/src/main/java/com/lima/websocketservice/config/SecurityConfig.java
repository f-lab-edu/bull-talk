package com.lima.websocketservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정
        .csrf(AbstractHttpConfigurer::disable) // CSRF 비활성화
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/**").permitAll() // 모든 경로 허용
            .requestMatchers("/swagger-ui.html/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
        )
        .headers(headers -> headers
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; connect-src 'self' ws://localhost:8084 wss://localhost:8084 http://localhost:8084 http://localhost:8000;")
            )
        );

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:8084", "http://localhost:8000")); // 8000 포트 허용
    configuration.addAllowedHeader("*"); // 모든 헤더 허용
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE")); // 허용된 HTTP 메소드
    configuration.setAllowCredentials(true); // 자격 증명 허용
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
