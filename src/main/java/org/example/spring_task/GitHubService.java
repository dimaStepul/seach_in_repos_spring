package org.example.spring_task;

import static org.example.spring_task.utils.GitHubApiBuilder.apiLinkForRepos;
import static org.example.spring_task.utils.GitHubApiBuilder.apiRepos;
import static org.example.spring_task.utils.GitHubApiBuilder.apiSearchInReadme;
import static org.example.spring_task.utils.GitHubApiBuilder.apiSearchOrgRepos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
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

  @Retryable(retryFor = {HttpClientErrorException.class,
      ResourceAccessException.class}, maxAttempts = 4,
      backoff = @Backoff(delay = 1000))
  private ResponseEntity<String> doRequest(String apiUrl, HttpEntity<String> entity) {
    try {
      logger.info("Getting request not from cache");
      return restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
    } catch (HttpClientErrorException clientErrorException) {
      logger.error("Client error: {}", clientErrorException.getMessage());
      throw clientErrorException;
    } catch (Exception error) {
      logger.error("Unexpected error: {}", error.getMessage());
      throw error;
    }
  }

  @Recover
  public String recover(HttpClientErrorException e) {
    logger.info("HttpClientErrorException recovered");
    return "Can not call API due to client error";
  }

  private String extractOrganizationName(String url) {
    String[] parts = url.split("/");
    return parts[parts.length - 1];
  }

  @Cacheable(cacheNames = "default", key = "'getRepos:' + #organizationLink + ':' + #accessToken + ':' + #targetWord")
  public GitHubRepos getRepos(String organizationLink, String accessToken, String targetWord) {
    String organizationName = extractOrganizationName(organizationLink);
    logger.info("organizationName" + organizationName);
    HashSet<String> allRepositories = getAllRepositories(organizationName, accessToken);
    if (allRepositories.isEmpty()) {
      return new GitHubRepos(Collections.emptySet(), Collections.emptySet());
    }
    HashSet<String> filteredRepositories = getFilteredRepositories(organizationName, accessToken,
        targetWord);

    logger.info("allRepositories:");
    allRepositories.forEach(logger::info);
    logger.info("filteredRepositories:");
    filteredRepositories.forEach(logger::info);

    if (!filteredRepositories.isEmpty()) {
      allRepositories.removeAll(filteredRepositories);
    }
    return new GitHubRepos(filteredRepositories, allRepositories);
  }

  private HashSet<String> getAllRepositories(String organizationName, String accessToken) {
    String allReposUrl = apiLinkForRepos + organizationName + apiRepos;
    logger.info("allReposUrl" + allReposUrl);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<String> allReposResponse = doRequest(allReposUrl, entity);
    return extractRepositoryNames(allReposResponse);
  }

  private HashSet<String> getFilteredRepositories(String organizationName, String accessToken,
      String targetWord) {
    String searchReposUrl =
        apiSearchOrgRepos + organizationName + "+" + targetWord
            + apiSearchInReadme;
    logger.info("searchReposUrl" + searchReposUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    ResponseEntity<String> searchReposResponse = doRequest(searchReposUrl, entity);
    return extractRepositoryNames(searchReposResponse);
  }


  private HashSet<String> extractRepositoryNames(ResponseEntity<String> response) {
    HashSet<String> repositoryNames = new HashSet<>();
    if (!response.getStatusCode().is2xxSuccessful()) {
      return repositoryNames;
    }
    String responseBody = response.getBody();
    if (responseBody == null) {
      return repositoryNames;
    }
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      JsonNode root;
      if (responseBody.startsWith("[")) {
        root = objectMapper.readTree(responseBody);
      } else {
        root = objectMapper.readTree(responseBody).get("items");
      }
      if (root == null || !root.isArray()) {
        return repositoryNames;
      }
      for (JsonNode item : root) {
        JsonNode fullNameNode = item.get("full_name");
        if (fullNameNode != null) {
          repositoryNames.add(fullNameNode.asText());
        }
      }
    } catch (JsonProcessingException e) {
      logger.error("Error processing JSON response: {}", e.getMessage());
    }
    return repositoryNames;
  }

}
