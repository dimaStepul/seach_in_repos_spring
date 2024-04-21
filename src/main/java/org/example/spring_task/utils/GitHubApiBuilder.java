package org.example.spring_task.utils;

public class GitHubApiBuilder {

  public final static String apiLinkForRepos = "https://api.github.com/orgs/";
  public final static String apiRepos = "/repos";

  public final static String apiSearchOrgRepos = "https://api.github.com/search/repositories?q=org:";
  public final static String apiSearchInReadme = "+in:readme";

  // example of request to  get all repos from organization:
  //  https://api.github.com/orgs/skibidi-toilets/repos

  // example of request to GitHub rest api to get all repos with specific word in readme:
  //  https://api.github.com/search/repositories?q=org:skibidi-toilets+hello+in:readme"
}
