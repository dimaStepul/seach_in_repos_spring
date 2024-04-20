package org.example.spring_task;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

  @ExceptionHandler(Throwable.class)
  public String handleError(Throwable e, Model model) {
    ErrorHandler errorHandler = ErrorHandler.getErrorType(e);
    model.addAttribute("errorType", errorHandler);
    model.addAttribute("errorCode", errorHandler.name());
    model.addAttribute("errorMessage", e.getMessage());
    return errorHandler.getErrorPage();
  }

  @GetMapping("/form")
  public String showForm(Model model) {
    model.addAttribute("githubForm", new GitHubForm());
    return "form";
  }

  @PostMapping("/form")
  public String sumbitForm(@ModelAttribute GitHubForm gitHubForm, Model model) {
    try {
      GitHubRepos repos = gitHubService.getRepos(gitHubForm.getOrganization(),
          gitHubForm.getAccessToken(), gitHubForm.getTargetWord());


      model.addAttribute("reposWithHello", repos.reposWithHello());
      model.addAttribute("reposWithoutHello", repos.reposWithoutHello());
    } catch (Exception e) {
      return handleError(e, model);
    }
    return "answer_page";
  }
}

