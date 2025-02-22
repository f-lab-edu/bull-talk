package com.lima.consoleservice.config.security;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity // Spring Security 활성화
// 메서드 수준에서 @PreAuthorize, @Secured, @RolesAllowed 등을 사용하여 권한 제한
// 메서드 호출에 대해 인증 및 권한 체크
//@EnableMethodSecurity
public class SecurityConfig {

  private final JwtFilter jwtFilter;

  // JwtFilter를 생성자 주입 받음
  public SecurityConfig(JwtFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }


  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
    http
        .cors(cors -> corsConfigurationSource())
        .csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login").permitAll()
                .requestMatchers("/swagger-ui.html/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/v1/user/signup").permitAll()
                .requestMatchers("/api/v1/user/login").permitAll()
                .requestMatchers("/api/v1/user/logout").permitAll()
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
        );
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:8083"));
    configuration.addAllowedHeader("*");
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH", "DELETE"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
