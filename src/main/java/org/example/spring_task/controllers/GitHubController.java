package org.example.spring_task.controllers;

import org.example.spring_task.GitHubForm;
import org.example.spring_task.GitHubRepos;
import org.example.spring_task.services.GitHubService;
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
  public String submitForm(@ModelAttribute GitHubForm gitHubForm, Model model) {
    GitHubRepos repos = gitHubService.getRepos(gitHubForm.getOrganization(),
        gitHubForm.getAccessToken(), gitHubForm.getTargetWord());


    model.addAttribute("reposWithHello", repos.reposWithHello());
    model.addAttribute("reposWithoutHello", repos.reposWithoutHello());
    if (repos.reposWithHello().isEmpty() && repos.reposWithoutHello().isEmpty()) {
      return "no_repos_page";
    }
    else {
      return "answer_page";
    }
  }
}

