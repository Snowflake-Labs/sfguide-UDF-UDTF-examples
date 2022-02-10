package com.snowflake.redteam.uarlos;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class IamPolicyCacheKey {

  static IamPolicyCacheKey fromString(String value) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      return new IamPolicyCacheKey(
          digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (NoSuchAlgorithmException err) {
      throw new RuntimeException(err);
    }
  }

  private IamPolicyCacheKey(byte[] hashedKey) { this.hashedKey = hashedKey; }

  @Override
  public boolean equals(Object o) {
    if (o == this)
      return true;
    if (!(o instanceof IamPolicyCacheKey)) {
      return false;
    }
    IamPolicyCacheKey iamPolicyCacheKey = (IamPolicyCacheKey)o;
    return Arrays.equals(hashedKey, iamPolicyCacheKey.hashedKey);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(hashedKey);
  }
  private byte[] hashedKey;

}
