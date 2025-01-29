package com.lima.consoleservice.config.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Hasher {

  private String hashingAlgorithm;

  public Hasher(String hashingAlgorithm) {
    this.hashingAlgorithm = hashingAlgorithm;
  }

  public String getHashingValue(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance(hashingAlgorithm);
      byte[] hash = digest.digest(password.getBytes());
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Hash Failed",  e);
    }
  }
}

