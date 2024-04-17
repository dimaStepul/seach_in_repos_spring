package org.example.spring_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class GitHubController {

  private final GitHubService gitHubService;

  public GitHubController(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  @GetMapping("/form")
  public String showForm(Model model) {
    model.addAttribute("githubForm", new GitHubForm());
    return "form";
  }

  @PostMapping("/form")
  public String sumbitForm(@ModelAttribute GitHubForm gitHubForm, Model model)
      throws JsonProcessingException {
    GitHubRepos repos = gitHubService.getRepos(gitHubForm.getOrganization(),
        gitHubForm.getAccessToken(), gitHubForm.getTargetWord());

    HashSet<String> reposWithoutHello = repos.reposWithoutHello;
    HashSet<String> reposWithHello = repos.reposWithHello;

    model.addAttribute("reposWithHello", reposWithHello);
    model.addAttribute("reposWithoutHello", reposWithoutHello);

    return "answer_page";
  }
}

