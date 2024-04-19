package org.example.spring_task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class GitHubControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void testGetRepos() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.post("/form")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .param("organization", "dmitrii239")
            .param("accessToken", "2281337239")
            .param("targetWord", "hello"))
        .andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
        .andExpect(MockMvcResultMatchers.view().name("unauthorized_error.html"));
  }
}
