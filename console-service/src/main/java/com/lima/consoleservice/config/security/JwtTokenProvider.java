package com.lima.consoleservice.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

  public static final long ON_MINUTE_TO_MILLIS = 60 * 1000L;
  private String secretKey;
  private long tokenTimeForMinute;

  @Value("${jwt.secret-key}")
  public void setSecretKey(String secretKey) {
    this.secretKey = secretKey;
  }

  @Value("${jwt.token-time}")
  public void setTokenTimeForMinute(long tokenTime) {
    this.tokenTimeForMinute = tokenTime;
  }

  // jwt 토큰 생성
  public String createToken(String email) {
    return JWT.create()
        .withSubject(email)
        .withIssuedAt(new Date())
        .withExpiresAt(new Date(System.currentTimeMillis() + this.tokenTimeForMinute * ON_MINUTE_TO_MILLIS))
        .sign(Algorithm.HMAC256(this.secretKey));
  }

  // jwt 토큰에서 클레임 추출
  private DecodedJWT extractClaim(String token) {
    return JWT.require(Algorithm.HMAC256(this.secretKey))
        .build()
        .verify(token);
  }

  // 추출한 클레임에서 email 추출
  public String extractEmail(String token) {
    return extractClaim(token).getSubject();
  }

  // 토큰 만료인지 확인
  public boolean isTokenExpired(String token) {
    return extractClaim(token).getExpiresAt().before(new Date());
  }

  // 토큰 유효성 검즘
  public boolean validateToken(String token, String email) {
    return (email.equals(extractEmail(token)) && !isTokenExpired(token));
  }
}

