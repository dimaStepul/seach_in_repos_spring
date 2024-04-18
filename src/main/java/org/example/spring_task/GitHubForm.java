package org.example.spring_task;

public class GitHubForm {

  private String organization;
  private String accessToken;

  private String targetWord;

  public String getTargetWord() {
    return targetWord;
  }

  public void setTargetWord(String word) {
    if (word.isEmpty()) {
      targetWord = null;
    } else {
      this.targetWord = word;
    }
  }

  public String getOrganization() {
    return organization;
  }

  public void setOrganization(String organization) {
    this.organization = organization;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }
}
