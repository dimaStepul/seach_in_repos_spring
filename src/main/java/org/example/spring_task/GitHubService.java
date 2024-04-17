package org.example.spring_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.HashSet;
import java.util.Objects;
import org.example.spring_task.utils.GitHubApi;
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

  public GitHubRepos getRepos(String organizationLink, String accessToken, String targetWord)
      throws JsonProcessingException {
    String organizationName = extractOrganizationName(organizationLink);
    String apiUrl = GitHubApi.apiLinkMain
        + GitHubApi.apiOrgs
        + organizationName
        + GitHubApi.apiRepos;
    System.out.println("sfdsfs" + apiUrl);
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<List<Map<String, Object>>> response;


    try {
      response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity,
          new ParameterizedTypeReference<>() {
          });
    } catch (HttpClientErrorException.NotFound e) {
      System.out.println("Organization not found or has no repositories.");
      return null;
    } catch (HttpClientErrorException.BadRequest badRequest) {
      System.out.println("bad request");
      return null;
    } catch (HttpClientErrorException.Unauthorized e) {
      System.out.println("bad token ");
      return null;
    }

    HashSet<String> repositoriesWithHello = new HashSet<>();
    HashSet<String> repositoriesWithoutHello = new HashSet<>();

    if (!response.getStatusCode().is2xxSuccessful()) {
      return null;
    }

    List<Map<String, Object>> reposFromResponse = response.getBody();
    for (Map<String, Object> repo : reposFromResponse) {
      String readmeUrl = repo.get("contents_url").toString()
          .replace("{+path}", "README.md");
      ResponseEntity<String> readmeResponse;

      try {
        readmeResponse = restTemplate.exchange(readmeUrl, HttpMethod.GET, entity, String.class);
      } catch (HttpClientErrorException.NotFound e) {
        System.out.println("README.md not found in repository: " + repo.get("full_name"));
        repositoriesWithoutHello.add((String) repo.get("full_name"));
        continue;
      }




      if (!readmeResponse.getStatusCode().is2xxSuccessful()) {
        break;
      }
      JsonNode jsonNode = marshallResponseBody(readmeResponse);

      String encoding = jsonNode.get("encoding").asText();
      String base64Content = jsonNode.get("content").asText();
      base64Content = base64Content.replaceAll("\\s", "");
      if ("base64".equals(encoding)) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
        base64Content = new String(decodedBytes);
      }
      base64Content = base64Content.toLowerCase();
      System.out.println("Targe word " + "  " + targetWord);
//      System.out.println("ffsdfsdf" + "  " + base64Content);
      if (Objects.requireNonNull(base64Content).contains(targetWord.toLowerCase())) {
        System.out.println("added to with hello");
        repositoriesWithHello.add((String) repo.get("full_name"));
        repositoriesWithoutHello.remove((String) repo.get("full_name"));
      } else {
        repositoriesWithoutHello.add((String) repo.get("full_name"));
      }
    }

    return new GitHubRepos(repositoriesWithHello, repositoriesWithoutHello);
  }
}
