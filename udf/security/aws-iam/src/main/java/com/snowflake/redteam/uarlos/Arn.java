package com.snowflake.redteam.uarlos;


// https://docs.aws.amazon.com/quicksight/latest/APIReference/qs-arn-format.html

/**
 * Roughly provides a representation of ARN resources for the purposes of IAM
 * policy simulation.
 */
class Arn {
  private final String arnAsString;

  public Arn(String arn) { this.arnAsString = arn; }

  public boolean isValid() {
    try {
      this.getType();
      this.getTypeSuffix();
      this.getAccount();
      this.getPath();
      return true;
    } catch (Exception err) {
      return false;
    }
  }

  /**
   * Gets the principal referenced to by this ARN. If the ARN represents a root
   * level account, the account ID is returned instead of the principal.
   * 
   * @return the account ID or arn of the principal.
   */
  public String getPrincipal() {
    if (isAccountPrincipal()) {
      return this.getAccount();
    } else {
      return this.getArn();
    }
  }

  /**
   * Determines whether this ARN represents an individual resource, or an
   * account wide grant.
   * 
   * @return the type of ARN 
   */
  public PermissionGrant.Type getPrincipalType() {
    if (isAccountPrincipal()) {
      return PermissionGrant.Type.ACCOUNT;
    } else {
      return PermissionGrant.Type.ARN;
    }
  }

  /**
   * Determines if an ARN is an account wide principal.
   * 
   * @return true if so, false otherwise.
   */
  public boolean isAccountPrincipal() {
    return getType().equals("arn:aws:iam") && getPath().equals("root");
  }

  public String getArn() { return this.arnAsString; }

  public String getType() { return this.arnAsString.split("::", 2)[0]; }

  private String getTypeSuffix() { return this.arnAsString.split("::", 2)[1]; }

  public String getAccount() { return getTypeSuffix().split(":", 2)[0]; }

  public String getPath() { return getTypeSuffix().split(":", 2)[1]; }

  /**
   * Checks to see if a given ARN matches.  If this ARN is an account wide grant
   * this will check to see if the accounts match.
   * 
   * @param other the ARN being compared against
   * @return true if matches, false otherwise.
   */
  public boolean matches(Arn other) {
    if (isAccountPrincipal()) {
      return getAccount().equals(other.getAccount());
    }
    return arnAsString.equals(other.getArn());
  }
}