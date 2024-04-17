package org.example.spring_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.Objects;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitHubService {

  private final RestTemplate restTemplate;

  public GitHubService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  private boolean isBase64Encoded(String content) {
    try {
      Base64.getDecoder().decode(content);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private String decodeBase64(String content) {
    byte[] decodedBytes = Base64.getDecoder().decode(content);
    return new String(decodedBytes);
  }
  public List<String> getRepositoriesWithHelloReadme(String organization, String accessToken)
      throws JsonProcessingException {
    String apiUrl = "https://api.github.com/orgs/" + organization + "/repos";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<Map<String, Object>>> response;
    try {
      response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
    } catch (HttpClientErrorException.NotFound e) {
      // Handle 404 error (Not Found)
      System.out.println("Organization not found or has no repositories.");
      return new ArrayList<>(); // Return empty list
    }

    List<String> repositoriesWithHello = new ArrayList<>();
    if (response.getStatusCode().is2xxSuccessful()) {
      List<Map<String, Object>> repositories = response.getBody();
      for (Map<String, Object> repo : repositories) {
        String readmeUrl = (String) repo.get("contents_url").toString().replace("{+path}", "README.md");
        ResponseEntity<String> readmeResponse;
        try {
          readmeResponse = restTemplate.exchange(readmeUrl, HttpMethod.GET, entity, String.class);
        } catch (HttpClientErrorException.NotFound e) {
          // Handle 404 error (Not Found) for readme file
          System.out.println("README.md not found in repository: " + repo.get("full_name"));
          continue; // Continue to the next repository
        }

        if (readmeResponse.getStatusCode().is2xxSuccessful()) {
          String readmeContent = readmeResponse.getBody();

          String jsonString = readmeContent.substring(readmeContent.indexOf('{'));
          ObjectMapper objectMapper = new ObjectMapper();

          JsonNode jsonNode = objectMapper.readTree(jsonString);

          String encoding = jsonNode.get("encoding").asText();
          String base64Content = jsonNode.get("content").asText();
          base64Content = base64Content.replaceAll("\\s", "");

          System.out.println("README.md:          " + base64Content +   repo.get("full_name"));
          if ("base64".equals(encoding)) {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            readmeContent = new String(decodedBytes);
          }
          System.out.println("\n\n" + readmeContent +   repo.get("full_name"));
          if (Objects.requireNonNull(readmeContent).contains("HELLO")) {
            System.out.println("fsdfsdfsdfsdf");
            repositoriesWithHello.add((String) repo.get("full_name"));
          }
        }
      }
    }

    return repositoriesWithHello;
  }


  public List<String> getAllRepositories(String organization, String accessToken) {
    String apiUrl = "https://api.github.com/orgs/" + organization + "/repos";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

    List<String> repositories = new ArrayList<>();
    if (response.getStatusCode().is2xxSuccessful()) {
      List<Map<String, Object>> repos = response.getBody();
      for (Map<String, Object> repo : repos) {
        repositories.add((String) repo.get("full_name"));
      }
    }

    return repositories;
  }
}
