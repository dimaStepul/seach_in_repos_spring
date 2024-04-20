package org.example.spring_task;


import java.util.Set;


public record GitHubRepos(Set<String> reposWithHello, Set<String> reposWithoutHello) {

}
