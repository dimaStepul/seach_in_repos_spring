package org.example.spring_task;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;

@SpringBootTest
@AutoConfigureMockMvc
class GitHubControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private GitHubService gitHubService;

  @Test
  void testInternalServerError() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/form")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("organization", "dmitrii239")
            .param("accessToken", "2281337239")
            .param("targetWord", "hello"))
        .andExpect(MockMvcResultMatchers.status().is5xxServerError())
        .andExpect(MockMvcResultMatchers.view().name("general_error_page"));
  }


  @Test
  public void testGetRepos() throws Exception {
    Set<String> reposWithHello = new HashSet<>(Set.of("repo1", "repo2"));
    Set<String> reposWithoutHello = new HashSet<>(Set.of("repo3", "repo4"));
    GitHubRepos gitHubRepos = new GitHubRepos(reposWithHello, reposWithoutHello);

    when(gitHubService.getRepos("organization", "accessToken", "targetWord")).thenReturn(
        gitHubRepos);

    mockMvc.perform(MockMvcRequestBuilders.post("/form")
            .param("organization", "organization")
            .param("accessToken", "accessToken")
            .param("targetWord", "targetWord"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("answer_page"))
        .andExpect(MockMvcResultMatchers.model().attribute("reposWithHello", reposWithHello))
        .andExpect(MockMvcResultMatchers.model().attribute("reposWithoutHello", reposWithoutHello));
  }

  @Test
  public void testEmptyRepos() throws Exception {
    GitHubRepos gitHubRepos = new GitHubRepos(new HashSet<>(), new HashSet<>());
    when(gitHubService.getRepos("emptyOrg", "emptyToken", "emptyWord")).thenReturn(gitHubRepos);

    mockMvc.perform(MockMvcRequestBuilders.post("/form")
            .param("organization", "emptyOrg")
            .param("accessToken", "emptyToken")
            .param("targetWord", "emptyWord"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.view().name("no_repos_page"))
        .andExpect(
            MockMvcResultMatchers.model().attribute("reposWithHello", Collections.emptySet()))
        .andExpect(
            MockMvcResultMatchers.model().attribute("reposWithoutHello", Collections.emptySet()));
  }

  @Test
  public void testInvalidRepos() throws Exception {
    when(gitHubService.getRepos("invalidOrg", "invalidToken", "invalidWord")).thenThrow(
        new RuntimeException("Invalid data"));

    mockMvc.perform(MockMvcRequestBuilders.post("/form")
            .param("organization", "invalidOrg")
            .param("accessToken", "invalidToken")
            .param("targetWord", "invalidWord"))
        .andExpect(MockMvcResultMatchers.status().isInternalServerError())
        .andExpect(MockMvcResultMatchers.view().name("general_error_page"));
  }


  @Test
  public void testUnauthorizedRepos() throws Exception {
    when(gitHubService.getRepos("unauthorizedOrg", "invalidToken", "validWord"))
        .thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

    mockMvc.perform(MockMvcRequestBuilders.post("/form")
            .param("organization", "unauthorizedOrg")
            .param("accessToken", "invalidToken")
            .param("targetWord", "validWord"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized())
        .andExpect(MockMvcResultMatchers.view().name("unauthorized_error"));
  }


  @Test
  public void testNoOrg() throws Exception {
    when(gitHubService.getRepos("", "invalidToken", "validWord"))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND, "NOT_FOUND"));

    mockMvc.perform(MockMvcRequestBuilders.post("/form")
            .param("organization", "")
            .param("accessToken", "invalidToken")
            .param("targetWord", "validWord"))
        .andExpect(MockMvcResultMatchers.status().isNotFound())
        .andExpect(MockMvcResultMatchers.view().name("not_found_error_page"));
  }


}
