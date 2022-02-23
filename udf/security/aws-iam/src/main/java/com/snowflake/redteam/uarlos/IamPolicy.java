package com.snowflake.redteam.uarlos;

import com.google.gson.annotations.SerializedName;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class IamPolicy {
  @SerializedName("Statement") private IamStatement[] statements;

  public void setStatements(IamStatement[] statements) {
    this.statements = statements;
  }

  public IamStatement[] getStatements() { return this.statements; }

  /**
   * Simulates an AWS action against the statements in this IAM policy.
   *
   * @param req the request to simulate
   * @return an optional containing the effect of this policy, if any
   */
  public Optional<IamStatementEvaluationResult> simulateAction(IamRequest req) {
    Set<IamStatementEvaluationResult> results =
        Arrays.stream(statements)
            .map(stmt -> stmt.simulateAction(req))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());

    for (IamStatementEvaluationResult result : results) {
      if (result.getEffect() == IamStatement.Effect.DENY) {
        return Optional.of(result);
      }
    }

    for (IamStatementEvaluationResult result : results) {
      if (result.getEffect() == IamStatement.Effect.ALLOW) {
        return Optional.of(result);
      }
    }

    return Optional.empty();
  }

  /**
   * Return the list of ARNs which are allowed to undertake an action.
   *
   * This is useful for resource based IAM policies and AssumeRolePolicy
   * documents which specify a principal.
   *
   * @param req the IAM request to simulate.
   * @return an array of the authorized resource ARN principals.
   */
  public String[] getAllowedArns(IamRequest req) {
    return Arrays.stream(statements)
        .filter(stmt -> {
          Optional<IamStatementEvaluationResult> res = stmt.simulateAction(req);
          return res.isPresent() &&
              res.get().getEffect() == IamStatement.Effect.ALLOW;
        })
        .map(stmt -> Arrays.asList(stmt.getPrincipal().getReferences()))
        .flatMap(List::stream)
        .map(Arn::new)
        .filter(Arn::isValid)
        .filter(arn -> arn.getPrincipalType() == PermissionGrant.Type.ARN)
        .map(Arn::getArn)
        .collect(Collectors.toList())
        .toArray(new String[0]);
  }

  /**
   * Return the list of accounts from account-wide grants which are allowed to
   * undertake an action.
   *
   * This is useful for resource based IAM policies and AssumeRolePolicy
   * documents which specify a principal.
   *
   * @param req the IAM request to simulate.
   * @return an array of the authorized resource ARN principals.
   */
  public String[] getAllowedAccounts(IamRequest req) {
    return Arrays.stream(statements)
        .filter(stmt -> {
          Optional<IamStatementEvaluationResult> res = stmt.simulateAction(req);
          return res.isPresent() && res.get().getEffect() == IamStatement.Effect.ALLOW;
        })
        .map(stmt -> Arrays.asList(stmt.getPrincipal().getReferences()))
        .flatMap(List::stream)
        .map(Arn::new)
        .filter(Arn::isValid)
        .filter(arn -> arn.getPrincipalType() == PermissionGrant.Type.ACCOUNT)
        .map(Arn::getAccount)
        .collect(Collectors.toList())
        .toArray(new String[0]);
  }
}