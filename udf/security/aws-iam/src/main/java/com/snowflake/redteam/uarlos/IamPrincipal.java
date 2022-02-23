package com.snowflake.redteam.uarlos;

import java.util.Arrays;
import java.util.HashMap;

public class IamPrincipal extends HashMap<String, String[]> {
  public String[] getReferences() {
    if (containsKey("AWS")) {
      return get("AWS");
    }
    return new String[0];
  }

  public boolean includes(String principal) {
    // TODO(gerg): in the future we should check the principal type to see if
    // its an ARN or service and adjust this logic as appropriate.
    return Arrays.stream(getReferences()).anyMatch(r -> r.equals(principal));
  }
}
