package org.example.spring_task.utils;

import java.util.ArrayList;
import java.util.List;

public class GitHubApiBuilder {

  private static final String BASE_URL = "https://api.github.com/";

  public static SearchInOrgBuilder searchBuilder(String organizationName) {
    return new SearchInOrgBuilder(organizationName);
  }

  public static AllReposBuilder allReposBuilder(String organizationName) {
    return new AllReposBuilder(organizationName);
  }

  public static class SearchInOrgBuilder {

    private final String organizationName;
    private final List<String> keywords;

    private SearchInOrgBuilder(String organizationName) {
      this.organizationName = organizationName;
      this.keywords = new ArrayList<>();
    }

    public SearchInOrgBuilder withKeyword(String keyword) {
      this.keywords.add(keyword);
      return this;
    }

    public String build() {
      String query = String.join("+", keywords);
      return String.format("%ssearch/repositories?q=org:%s+%s+in:readme", GitHubApiBuilder.BASE_URL,
          organizationName, query);
    }
  }

  public static class AllReposBuilder {

    private final String organizationName;

    private AllReposBuilder(String organizationName) {
      this.organizationName = organizationName;
    }

    public String build() {
      return String.format("%sorgs/%s/repos", GitHubApiBuilder.BASE_URL, organizationName);
    }
  }
}

// example of request to  get all repos from organization:
//  https://api.github.com/orgs/skibidi-toilets/repos

// example of request to GitHub rest api to get all repos with specific word in readme:
//  https://api.github.com/search/repositories?q=org:skibidi-toilets+hello+in:readme"
