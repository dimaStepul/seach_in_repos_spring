package org.example.spring_task;


import java.util.HashSet;


public class GitHubRepos {
  public final HashSet<String> reposWithHello;
  public final HashSet<String> reposWithoutHello;

  public GitHubRepos(HashSet<String> reposWithHello, HashSet<String> reposWithoutHello) {
    this.reposWithHello = reposWithHello;
    this.reposWithoutHello = reposWithoutHello;
  }

}
