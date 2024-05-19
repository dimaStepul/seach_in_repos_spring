package org.example.spring_task.services;


import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.spring_task.GitHubRepos;
import org.example.spring_task.services.GitHubResponse.Item;
import org.example.spring_task.utils.GitHubApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {

  private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

  private final RestTemplate restTemplate;

  public GitHubService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  @Cacheable(cacheNames = "default", key = "'getRepos:' + #organizationLink + ':' + #accessToken + ':' + #targetWord")
  public GitHubRepos getRepos(String organizationLink, String accessToken, String targetWord) {
    String organizationName = extractOrganizationName(organizationLink);
    logger.info("organizationName: {}", organizationName);
    Set<String> allRepositories = getAllRepositories(organizationName, accessToken);
    if (allRepositories.isEmpty()) {
      return new GitHubRepos(Collections.emptySet(), Collections.emptySet());
    }
    Set<String> filteredRepositories = getFilteredRepositories(organizationName, accessToken,
        targetWord);

    logger.info("allRepositories : {}", allRepositories);
    logger.info("filteredRepositories : {}", filteredRepositories);

    if (!filteredRepositories.isEmpty()) {
      allRepositories.removeAll(filteredRepositories);
    }
    return new GitHubRepos(filteredRepositories, allRepositories);
  }

  @Recover
  private String recover(HttpClientErrorException e) {
    logger.info("HttpClientErrorException recovered");
    return "Can not call API due to client error";
  }

  private String extractOrganizationName(String url) {
    String[] parts = url.split("/");
    return parts[parts.length - 1];
  }

  @Retryable(retryFor = {HttpClientErrorException.class,
      ResourceAccessException.class}, maxAttempts = 4,
      backoff = @Backoff(delay = 1000))
  private Set<String> getAllRepositories(String organizationName, String accessToken) {
    String allReposUrl = GitHubApiBuilder.allReposBuilder(organizationName).build();
    logger.info("allReposUrl: {}", allReposUrl);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<List<Item>> responseEntity =
        restTemplate.exchange(
            allReposUrl,
            HttpMethod.GET,
            entity,
            new ParameterizedTypeReference<>() {
            }
        );
    List<Item> allRepos = responseEntity.getBody();
    if (allRepos == null) {
      return Collections.emptySet();
    }
    return allRepos.stream()
        .map(Item::getFullName)
        .collect(Collectors.toSet());
  }

  @Retryable(retryFor = {HttpClientErrorException.class,
      ResourceAccessException.class}, maxAttempts = 4,
      backoff = @Backoff(delay = 1000))
  private Set<String> getFilteredRepositories(String organizationName, String accessToken,
      String targetWord) {
    String searchReposUrl = GitHubApiBuilder.searchBuilder(organizationName)
        .withKeyword(targetWord)
        .build();
    logger.info("searchReposUrl: {}", searchReposUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<GitHubResponse> responseEntity =
        restTemplate.exchange(searchReposUrl, HttpMethod.GET, entity, GitHubResponse.class);
    GitHubResponse filteredRepos = responseEntity.getBody();
    if (filteredRepos == null) {
      return Collections.emptySet();
    }
    return filteredRepos.getItems().stream()
        .map(Item::getFullName).collect(Collectors.toSet());
  }
}
