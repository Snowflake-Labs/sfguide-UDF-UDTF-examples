package com.snowflake.redteam.uarlos;

import java.time.Instant;

class IamPolicyCacheEntry {
  private Instant lastUsed;
  private final IamPolicy policy;

  IamPolicyCacheEntry(IamPolicy policy) { this.policy = policy; }

  IamPolicy getPolicy() {
    this.lastUsed = Instant.now();
    return this.policy;
  }

  Instant getLastUsed() { return this.lastUsed; }
}