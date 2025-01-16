package com.lima.consoleservice.config;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class Hasher {

  private static final String HASHING_ALGORITHM = "SHA-256";

  public String getHashingValue(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance(HASHING_ALGORITHM);
      byte[] hash = digest.digest(password.getBytes());
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Hash Failed",  e);
    }
  }
}
