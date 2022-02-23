package com.snowflake.redteam.uarlos;

import java.util.HashMap;
import java.util.Map;

public class IamTrieNode {
  private Map<Character, IamTrieNode> next = new HashMap<>();
  private boolean wildcard = false;
  private boolean terminal = false;

  public boolean matches(String resource) {
    return matches(resource.toCharArray());
  }

  /**
   * Recursively search for a matching resource in the trie, including wildcard
   * matches.
   *
   * @param resource the resource to search for
   * @return true if a match is found, false otherwise.
   */
  public boolean matches(char[] resource) {
    if (this.wildcard) {
      return true;
    } else if (resource.length == 0 && this.terminal) {
      return true;
    } else if (resource.length > 0) {
      char cur = resource[0];
      if (!this.next.containsKey(cur)) {
        return false;
      }
      char[] rest = new char[resource.length - 1];
      for (int i = 1; i < resource.length; i++) {
        rest[i - 1] = resource[i];
      }
      return this.next.get(cur).matches(rest);
    }
    return false;
  }

  public void add(String resource) { add(resource.toCharArray()); }

  /**
   * Adds a new resource to the trie.
   *
   * @param resource the resource to add.
   */
  public void add(char[] resource) {
    if (resource.length == 1 && resource[0] == '*') {
      this.wildcard = true;
    } else if (resource.length == 0) {
      this.terminal = true;
    } else {
      char cur = resource[0];
      char[] rest = new char[resource.length - 1];
      for (int i = 1; i < resource.length; i++) {
        rest[i - 1] = resource[i];
      }
      this.next.computeIfAbsent(cur, k -> new IamTrieNode()).add(rest);
    }
  }
}