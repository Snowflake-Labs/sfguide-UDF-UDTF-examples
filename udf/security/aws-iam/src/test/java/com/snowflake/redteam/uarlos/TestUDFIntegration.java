package com.snowflake.redteam.uarlos;

import org.testng.annotations.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static org.testng.AssertJUnit.*;

@SuppressWarnings({"java:S5960"})
public class TestUDFIntegration {

  enum TestedAction {
    ASSUME_ROLE("sts:AssumeRole");
    private String value;
    public String getValue() { return this.value; }
    private TestedAction(String value) { this.value = value; }
  }

  /**
   * Utility function to read a whole resource as a String
   * @param path the path to the resource from this class.
   * @return a string containing the resource contents
   * @throws IOException
   */
  private static String resourceAsString(String path) throws IOException {
    try (InputStream resource =
             TestUDFIntegration.class.getResourceAsStream(path);
         Scanner scanner = new Scanner(resource, StandardCharsets.UTF_8);) {
      return scanner.useDelimiter("\\A").next();
    }
  }

  @Test
  public void testPolicySimulation() throws IOException {
    Map<String, String> policies = new HashMap<>();
    policies.put("policyArn", resourceAsString("assume_role_policy.json"));

    assertEquals(null, UDF.simulatePolicies(policies, "sts:EatBacon"));
    assertEquals(
        "{\"effectiveStatement\":{\"Principal\":{\"AWS\":[\"arn:aws:iam::123456789012:root\"]},\"Action\":[\"sts:AssumeRole\"],\"Effect\":\"Allow\"},\"policyName\":\"policyArn\",\"effect\":\"Allow\"}",
        UDF.simulatePolicies(policies, "sts:AssumeRole"));
  }

  @Test
  public void testAssumeRoleSingle() throws IOException {
    String policyDocument = resourceAsString("assume_role_policy.json");

    assertArrayEquals(new String[] {"123456789012"},
                      UDF.getAuthorizedAccounts(
                          policyDocument, TestedAction.ASSUME_ROLE.getValue()));
    assertArrayEquals(new String[0],
                      UDF.getAuthorizedArns(
                          policyDocument, TestedAction.ASSUME_ROLE.getValue()));
  }

  @Test
  public void testAssumeRoleList() throws IOException {
    String policyDocument = resourceAsString("assume_role_policy_list.json");
    assertArrayEquals(new String[] {"123456789013"},
                      UDF.getAuthorizedAccounts(
                          policyDocument, TestedAction.ASSUME_ROLE.getValue()));
    assertArrayEquals(new String[] {"arn:aws:iam::123456789014:users/foobar"},
                      UDF.getAuthorizedArns(
                          policyDocument, TestedAction.ASSUME_ROLE.getValue()));
  }

  @Test
  public void testAssumeRoleMultipleStatements() throws IOException {
    String policyDocument =
        resourceAsString("assume_role_policy_multi_statements.json");
    assertArrayEquals(new String[] {"123456789012", "123456789013"},
                      UDF.getAuthorizedAccounts(
                          policyDocument, TestedAction.ASSUME_ROLE.getValue()));
    assertArrayEquals(new String[] {"arn:aws:iam::123456789014:users/foobar"},
                      UDF.getAuthorizedArns(
                          policyDocument, TestedAction.ASSUME_ROLE.getValue()));
  }
}
