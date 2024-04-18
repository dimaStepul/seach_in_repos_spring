package org.example.spring_task;

import static org.example.spring_task.utils.GitHubApiBuilder.apiCodingAlgo;
import static org.example.spring_task.utils.GitHubApiBuilder.apiDecoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.example.spring_task.Exceptions.UnknownEncodingException;
import org.example.spring_task.utils.GitHubApiBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class GitHubService {


  private static final Logger logger = LoggerFactory.getLogger(GitHubService.class);

  private final RestTemplate restTemplate;

  public GitHubService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public static String extractOrganizationName(String url) {
    String[] parts = url.split("/");
    return parts[parts.length - 1];
  }


  private JsonNode marshallResponseBody(ResponseEntity<String> readmeResponse)
      throws JsonProcessingException {
    Objects.requireNonNull(readmeResponse, "empty response body when marshalling");
    String readmeContent = readmeResponse.getBody();
    String jsonString = readmeContent.substring(readmeContent.indexOf('{'));
    ObjectMapper objectMapper = new ObjectMapper();

    return objectMapper.readTree(jsonString);
  }

  private ResponseEntity<List<Map<String, Object>>> sendRequest(String apiUrl,
      HttpEntity<String> entity) {
    try {
      return restTemplate.exchange(apiUrl, HttpMethod.GET, entity,
          new ParameterizedTypeReference<>() {
          });
    } catch (HttpClientErrorException clientErrorException) {
      logger.error("Client error: {}", clientErrorException.getMessage());
      throw clientErrorException;
    } catch (Exception error) {
      logger.error("Unexpected error: {}", error.getMessage());
      throw error;
    }
  }

  private void processReadmeResponse(JsonNode jsonNode, String targetWord,
      HashSet<String> repositoriesWithHello, HashSet<String> repositoriesWithoutHello,
      Map<String, Object> repo) {
    String encoding = jsonNode.get("encoding").asText();
    String content = jsonNode.get("content").asText();

    content = content.replaceAll("\\s", "");
    if (apiCodingAlgo.equals(encoding)) {
      byte[] decodedBytes = apiDecoder.decode(content);
      content = new String(decodedBytes);
    } else {
      throw new UnknownEncodingException("Unknown encoding: " + encoding);
    }
    content = content.toLowerCase();

    logger.info("Target word: {}", targetWord);
    if (Objects.requireNonNull(content).contains(targetWord.toLowerCase())) {
      logger.info("Added repository with hello: {}", repo.get("full_name"));
      repositoriesWithHello.add((String) repo.get("full_name"));
      repositoriesWithoutHello.remove((String) repo.get("full_name"));
    } else {
      logger.info("Repository does not contain hello: {}", repo.get("full_name"));
      repositoriesWithoutHello.add((String) repo.get("full_name"));
    }
  }

  private void processRepository(Map<String, Object> repo, String targetWord,
      HttpEntity<String> entity,
      HashSet<String> repositoriesWithHello, HashSet<String> repositoriesWithoutHello)
      throws JsonProcessingException {

    String readmeUrl = repo.get("contents_url").toString()
        .replace("{+path}", "README.md");
    ResponseEntity<String> readmeResponse;

    try {
      readmeResponse = restTemplate.exchange(readmeUrl, HttpMethod.GET, entity, String.class);
    } catch (HttpClientErrorException.NotFound e) {
      logger.error("README.md not found in repository: {}", repo.get("full_name"));
      repositoriesWithoutHello.add((String) repo.get("full_name"));
      return;
    }

    if (!readmeResponse.getStatusCode().is2xxSuccessful() || !readmeResponse.hasBody()) {
      return;
    }

    JsonNode jsonNode = marshallResponseBody(readmeResponse);
    processReadmeResponse(jsonNode, targetWord, repositoriesWithHello, repositoriesWithoutHello,
        repo);
  }


  public GitHubRepos getRepos(String organizationLink, String accessToken, String targetWord)
      throws JsonProcessingException {

    String organizationName = extractOrganizationName(organizationLink);

    String apiUrl = GitHubApiBuilder.apiLinkMain
        + GitHubApiBuilder.apiOrgs
        + organizationName
        + GitHubApiBuilder.apiRepos;
    logger.info("apiUrl:  " + apiUrl);

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<Map<String, Object>>> response = sendRequest(apiUrl, entity);

    HashSet<String> repositoriesWithHello = new HashSet<>();
    HashSet<String> repositoriesWithoutHello = new HashSet<>();

    if (!response.getStatusCode().is2xxSuccessful()) {
      return null;
    }

    List<Map<String, Object>> reposFromResponse = response.getBody();
    for (Map<String, Object> repo : reposFromResponse) {
      processRepository(repo, targetWord, entity, repositoriesWithHello, repositoriesWithoutHello);
    }

    return new GitHubRepos(repositoriesWithHello, repositoriesWithoutHello);
  }
}
