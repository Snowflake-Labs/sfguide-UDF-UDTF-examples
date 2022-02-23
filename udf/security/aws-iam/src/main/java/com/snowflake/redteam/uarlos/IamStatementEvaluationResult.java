package com.snowflake.redteam.uarlos;


public class IamStatementEvaluationResult {

  public IamStatement.Effect getEffect() {
    return this.effect;
  }

  public IamStatement getEffectiveStatement() {
    return this.effectiveStatement;
  }

  public IamStatementEvaluationResult(IamStatement.Effect effect,
                             IamStatement effectiveStatement) {
    this.effect = effect;
    this.effectiveStatement = effectiveStatement;
  }
  private final IamStatement.Effect effect;
  private final IamStatement effectiveStatement;
}