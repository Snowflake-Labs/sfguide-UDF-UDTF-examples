package com.snowflake.redteam.uarlos;

import com.google.gson.annotations.SerializedName;
import java.util.Optional;

public class IamStatement {

  enum Effect {
    @SerializedName("Allow") ALLOW("Allow"),
    @SerializedName("Deny") DENY("Deny");
    private String value;
    public String getValue() { return this.value; }
    private Effect(String value) { this.value = value; }
  }

  @SerializedName("Principal") private IamPrincipal principal;

  @SerializedName("NotPrincipal") private IamPrincipal notPrincipal;

  @SerializedName("Resource") private String[] resource;

  @SerializedName("NotResource") private String[] notResource;

  @SerializedName("Action") private String[] action;

  @SerializedName("NotAction") private String[] notAction;

  @SerializedName("Effect") private Effect effect;

  //@SerializedName("Condition") private IamCondition condition;

  public IamPrincipal getPrincipal() { return this.principal; }

  public void setPrincipal(IamPrincipal principal) {
    this.principal = principal;
  }

  public IamPrincipal getNotPrincipal() { return this.notPrincipal; }

  public void setNotPrincipal(IamPrincipal notPrincipal) {
    this.notPrincipal = notPrincipal;
  }

  public String[] getResource() { return this.resource; }

  public void setResource(String[] resource) { this.resource = resource; }

  public String[] getNotResource() { return this.notResource; }

  public void setNotResource(String[] notResource) {
    this.notResource = notResource;
  }

  public String[] getAction() { return this.action; }

  public void setAction(String[] action) { this.action = action; }

  public String[] getNotAction() { return this.notAction; }

  public void setNotAction(String[] notAction) { this.notAction = notAction; }

  public Effect getEffect() { return this.effect; }

  public void setEffect(Effect effect) { this.effect = effect; }

  // public IamCondition getCondition() { return this.condition; }

  /*public void setCondition(IamCondition condition) {
    this.condition = condition;
  }*/

  public Optional<IamTrieNode> getActionTrie() {
    if (this.action == null)
      return Optional.empty();
    IamTrieNode trie = new IamTrieNode();
    for (String field : this.action) {
      trie.add(field);
    }
    return Optional.of(trie);
  }

  public Optional<IamTrieNode> getNotActionTrie() {
    if (this.notAction == null)
      return Optional.empty();
    IamTrieNode trie = new IamTrieNode();
    for (String field : this.notAction) {
      trie.add(field);
    }
    return Optional.of(trie);
  }

  public Optional<IamTrieNode> getResourceTrie() {
    if (this.resource == null)
      return Optional.empty();
    IamTrieNode trie = new IamTrieNode();
    for (String field : this.resource) {
      trie.add(field);
    }
    return Optional.of(trie);
  }

  public Optional<IamTrieNode> getNotResourceTrie() {
    if (this.notResource == null)
      return Optional.empty();
    IamTrieNode trie = new IamTrieNode();
    for (String field : this.notResource) {
      trie.add(field);
    }
    return Optional.of(trie);
  }

  /**
   * Checks if the IamRequest matches the Action or NotAction for this
   * statement.
   *
   * @param request
   * @return true if it matches, false otherwise.
   */
  boolean matchesAction(IamRequest request) {
    String requestedAction = request.getAction();
    Optional<IamTrieNode> actionTrie = getActionTrie();
    if (actionTrie.isPresent() && actionTrie.get().matches(requestedAction)) {
      return true;
    }
    Optional<IamTrieNode> notActionTrie = getNotActionTrie();
    return notActionTrie.isPresent() &&
        !notActionTrie.get().matches(requestedAction);
  }

  /**
   * Checks if the IamRequest matches the Resource or NotResource for this
   * statement.
   *
   * @param request
   * @return true if it matches, false otherwise.
   */
  boolean matchesResource(IamRequest request) {
    Optional<String> resourceOrEmpty = request.getResource();
    if (resourceOrEmpty.isEmpty()) {
      return true;
    }
    String requestedResource = resourceOrEmpty.get();

    Optional<IamTrieNode> resourceTrie = getResourceTrie();
    if (resourceTrie.isPresent() &&
        resourceTrie.get().matches(requestedResource)) {
      return true;
    }
    Optional<IamTrieNode> notResourceTrie = getNotResourceTrie();
    return notResourceTrie.isPresent() &&
        !notResourceTrie.get().matches(requestedResource);
  }

  /**
   * Checks if the IamRequest matches the Principal or NotPrincipal for this
   * statement.
   *
   * @param request
   * @return true if it matches, false otherwise.
   */
  boolean matchesPrincipal(IamRequest request) {
    // WTF?
    if (getPrincipal() == null) {
      return true;
    }

    Optional<String> requestedPrincipal = request.getPrincipal();
    if (requestedPrincipal.isEmpty()) {
      return true;
    }
    if (getPrincipal().includes(requestedPrincipal.get())) {
      return true;
    }
    return !getNotPrincipal().includes(requestedPrincipal.get());
  }

  /**
   * Checks if the IamRequest matches the conditions configured for this
   * statement. <strong>THIS IS NOT YET IMPLEMENTED AND ALWAYS RETURNS
   * true</strong>.
   *
   * @param request
   * @return true, always.
   */
  public boolean matchesCondition(IamRequest request) { return true; }

  public Optional<IamStatementEvaluationResult> simulateAction(IamRequest request) {
    if (matchesAction(request) && matchesResource(request) &&
        matchesPrincipal(request) && matchesCondition(request)) {
      return Optional.of(new IamStatementEvaluationResult(effect, this));
    }
    return Optional.empty();
  }
}