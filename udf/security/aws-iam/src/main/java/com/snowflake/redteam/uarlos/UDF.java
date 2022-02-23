package com.snowflake.redteam.uarlos;

import com.google.gson.Gson;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UDF {

  private UDF() {}

  /**
   * Simulates IAM policies based on data in Snowflake
   *
   * The caller should have previously constructed an map of relevant IAM policy
   * documents keyed by policy ARN to simulate and then pass that array along
   * with the requested action.  Conditions are <strong>not</strong> currently
   * simulated.
   *
   * @param policyDocuments a map of policyArn, policyDocument
   * @param action the AWS action that is being performed
   * @param resource the ARN of the resource receiving the action
   * @param principal the ARN of the principal initiating the action
   * @return true if the policies allow the action, false otherwise
   */
  public static String simulatePolicies(Map<String, String> policyDocuments,
                                        String action, String resource,
                                        String principal) {
    return simulatePolicies(policyDocuments, new IamRequest.Builder()
                                                 .withAction(action)
                                                 .withResource(resource)
                                                 .withPrincipal(principal)
                                                 .build());
  }

  /**
   * Simulates IAM policies based on data in Snowflake
   *
   * The caller should have previously constructed an map of relevant IAM policy
   * documents keyed by policy ARN to simulate and then pass that array along
   * with the requested action.  Conditions are <strong>not</strong> currently
   * simulated.
   *
   * @param policyDocuments a map of policyArn, policyDocument
   * @param action the AWS action that is being performed
   * @param resource the ARN of the resource receiving the action
   * @return true if the policies allow the action, false otherwise
   */
  public static String simulatePolicies(Map<String, String> policyDocuments,
                                        String action, String resource) {
    return simulatePolicies(policyDocuments, new IamRequest.Builder()
                                                 .withAction(action)
                                                 .withResource(resource)
                                                 .build());
  }

  /**
   * Simulates IAM policies based on data in Snowflake
   *
   * The caller should have previously constructed an map of relevant IAM policy
   * documents keyed by policy ARN to simulate and then pass that array along
   * with the requested action.  Conditions are <strong>not</strong> currently
   * simulated.
   *
   * @param policyDocuments a map of policyArn, policyDocument
   * @param action the AWS action that is being performed
   * @return true if the policies allow the action, false otherwise
   */
  public static String simulatePolicies(Map<String, String> policyDocuments,
                                        String action) {
    return simulatePolicies(
        policyDocuments, new IamRequest.Builder().withAction(action).build());
  }

  private static String simulatePolicies(Map<String, String> policyDocuments,
                                         IamRequest req) {
    Map<String, Optional<IamStatementEvaluationResult>> results =
        policyDocuments.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, e -> {
              IamPolicy policy = IamPolicyFactory.fromString(e.getValue());
              return policy.simulateAction(req);
            }));

    Gson gson = new Gson();

    // DENY always wins.
    for (Map.Entry<String, Optional<IamStatementEvaluationResult>> entries :
         results.entrySet()) {
      Optional<IamStatementEvaluationResult> result = entries.getValue();
      if (result.isPresent() &&
          result.get().getEffect() == IamStatement.Effect.DENY) {
        return gson.toJson(IamSimulationResult.fromIamStatementResult(
            result.get(), entries.getKey()));
      }
    }

    // ALLOW if present.
    for (Map.Entry<String, Optional<IamStatementEvaluationResult>> entries :
         results.entrySet()) {
      Optional<IamStatementEvaluationResult> result = entries.getValue();
      if (result.isPresent() &&
          result.get().getEffect() == IamStatement.Effect.ALLOW) {
        return gson.toJson(IamSimulationResult.fromIamStatementResult(
            result.get(), entries.getKey()));
      }
    }

    return null;
  }

  /**
   * Simulates policies and returns a list of principals who are authorized from
   * an IAM resource policy or an Assume Role policy.
   *
   * @param policyDocument the policy document to simulate.
   * @param action the AWS action to simulate.
   * @param resource the ARN of the resource to simulate the action on.
   * @return the list of ARN resources who are allowed by the policy.
   */
  public static String[] getAuthorizedArns(String policyDocument, String action,
                                           String resource) {
    return IamPolicyFactory.fromString(policyDocument)
        .getAllowedArns(new IamRequest.Builder()
                            .withAction(action)
                            .withResource(resource)
                            .build());
  }

  /**
   * Simulates policies and returns a list of principals who are authorized from
   * an IAM resource policy or an Assume Role policy.
   *
   * @param policyDocument the policy document to simulate.
   * @param action the AWS action to simulate.
   * @return the list of ARN resources who are allowed by the policy.
   */
  public static String[] getAuthorizedArns(String policyDocument,
                                           String action) {
    return IamPolicyFactory.fromString(policyDocument)
        .getAllowedArns(new IamRequest.Builder().withAction(action).build());
  }

  /**
   * Simulates policies and returns a list of principals who are authorized from
   * an IAM resource policy or an Assume Role policy.
   *
   * @param policyDocument the policy document to simulate.
   * @param action the AWS action to simulate.
   * @param resource the ARN of the resource to simulate the action on.
   * @return the list of accounts who are allowed by the policy.
   */
  public static String[] getAuthorizedAccounts(String policyDocument,
                                               String action, String resource) {
    return IamPolicyFactory.fromString(policyDocument)
        .getAllowedAccounts(new IamRequest.Builder()
                                .withAction(action)
                                .withResource(resource)
                                .build());
  }

  /**
   * Simulates policies and returns a list of accounts who are authorized from
   * an IAM resource policy or an Assume Role policy.
   *
   * @param policyDocument the policy document to simulate.
   * @param action the AWS action to simulate.
   * @return the list of accounts who are allowed by the policy.
   */
  public static String[] getAuthorizedAccounts(String policyDocument,
                                               String action) {
    return IamPolicyFactory.fromString(policyDocument)
        .getAllowedAccounts(
            new IamRequest.Builder().withAction(action).build());
  }

  /**
   * Checks to see whether or not an ARN is accepted by a list of acceptors.
   * This is used to assist matching entities against account-wide grants.
   * @param candidate
   * @param acceptors
   * @return
   */
  public static Boolean arnAccepts(String candidate, String[] acceptors) {
    Arn candidateArn = new Arn(candidate);
    if (!candidateArn.isValid()) {
      return false;
    }

    return Arrays.stream(acceptors)
        .map(Arn::new)
        .filter(Arn::isValid)
        .anyMatch(a -> a.matches(candidateArn));
  }
}