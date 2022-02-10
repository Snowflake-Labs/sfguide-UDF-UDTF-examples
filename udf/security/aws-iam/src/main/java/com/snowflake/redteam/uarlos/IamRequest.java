package com.snowflake.redteam.uarlos;

import java.util.Optional;

public class IamRequest {
  private final String action;
  private final Optional<String> resource;
  private final Optional<String> principal;

  public static class Builder {
    private String action;
    private String resource;
    private String principal;

    public Builder withAction(String action) {
      this.action = action;
      return this;
    }
    public Builder withResource(String resource) {
      this.resource = resource;
      return this;
    }
    public Builder withPrincipal(String principal) {
      this.principal = principal;
      return this;
    }

    Builder() {}

    public IamRequest build() {
      return new IamRequest(this.action, Optional.ofNullable(this.resource),
                            Optional.ofNullable(this.principal));
    }
  }

  private IamRequest(String action, Optional<String> resource,
                     Optional<String> principal) {
    this.action = action;
    this.resource = resource;
    this.principal = principal;
  }

  public String getAction() { return this.action; }

  public Optional<String> getResource() { return this.resource; }

  public Optional<String> getPrincipal() { return this.principal; }
}
