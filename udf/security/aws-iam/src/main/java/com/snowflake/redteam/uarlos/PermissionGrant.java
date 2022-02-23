package com.snowflake.redteam.uarlos;

import java.util.HashMap;

public class PermissionGrant extends HashMap<String, String> {

  enum Type {
    ACCOUNT("account"),
    ARN("arn");
    private String value;
    Type(String value) { this.value = value; }
    public String getValue() { return this.value; }
  }

  public PermissionGrant addArn(Arn arn) {
    this.put(arn.getPrincipal(), arn.getPrincipalType().getValue());
    return this;
  }

  public PermissionGrant mergeGrant(PermissionGrant other) {
    for (Entry<String, String> entry : other.entrySet()) {
      this.put(entry.getKey(), entry.getValue());
    }
    return this;
  }
}
