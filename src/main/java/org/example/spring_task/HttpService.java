package org.example.spring_task;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class HttpService {

  private final RestTemplate restTemplate;

  public HttpService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String getGoogleHomePage() {
    String url = "https://www.google.com";
    return restTemplate.getForObject(url, String.class);
  }

  public String getExampleOrgHomePage() {
    String url = "https://example.org/";
    return restTemplate.getForObject(url, String.class);
  }
}
