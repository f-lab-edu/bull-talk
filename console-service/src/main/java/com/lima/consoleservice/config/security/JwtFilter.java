package com.lima.consoleservice.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;
  private final RedisTemplate<String, Object> redisTemplate;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = request.getHeader("Authorization");

    if (token != null && token.startsWith("Bearer ")) {
      token = token.substring(7); // Bearer 을 제외한 토큰만 추출.
      String email = jwtTokenProvider.extractEmail(token);
      // jwt 검증
      if (email != null && jwtTokenProvider.validateToken(token, email)) {
        // 권한을 추출하고 SimpleGrantedAuthority로 설정 (예: "ROLE_USER")
//        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"));
        // redis 에서 session 검증
        String storedToken = (String) redisTemplate.opsForValue().get("session: " + email);
        if (storedToken != null && storedToken.equals(token)) {
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null, null);
          SecurityContextHolder.getContext().setAuthentication(authentication );
        }
      }
    }
    // 필터 체인 실행
    filterChain.doFilter(request, response);
  }
}

