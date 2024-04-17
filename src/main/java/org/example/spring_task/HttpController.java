package org.example.spring_task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HttpController {

  private final HttpService httpService;

  @Autowired
  public HttpController(HttpService httpService) {
    this.httpService = httpService;
  }

  @GetMapping("/google")
  public String getGoogleHomePage() {
    return httpService.getGoogleHomePage();
  }

  @GetMapping("/example")
  public String getExampleOrgHomePage() {
    return httpService.getExampleOrgHomePage();
  }
}

