package com.snowflake.redteam.uarlos;

public class IamSimulationResult {

  public static IamSimulationResult
  fromIamStatementResult(IamStatementEvaluationResult result,
                         String policyName) {
    return new IamSimulationResult(result.getEffectiveStatement(),
                                   result.getEffect(), policyName);
  }

  public IamStatement getEffectiveStatement() {
    return this.effectiveStatement;
  }

  public String getPolicyName() { return this.policyName; }

  public IamStatement.Effect getEffect() { return this.effect; }

  public IamSimulationResult(IamStatement effectiveStatement,
                             IamStatement.Effect effect, String policyName) {
    this.effectiveStatement = effectiveStatement;
    this.policyName = policyName;
    this.effect = effect;
  }
  private final IamStatement effectiveStatement;
  private final String policyName;
  private final IamStatement.Effect effect;
}
