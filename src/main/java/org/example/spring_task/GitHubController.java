package org.example.spring_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import org.example.spring_task.utils.WordsExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class GitHubController {

  private final GitHubService gitHubService;

  public GitHubController(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  @GetMapping("/repositories")
  public List<String> getRepositories(@RequestParam String organization, @RequestParam String accessToken)
      throws JsonProcessingException {
    return gitHubService.getRepositoriesWithHelloReadme(organization, accessToken);
  }


  @GetMapping("/form")
  public String showForm(Model model) {
    model.addAttribute("githubForm", new GitHubForm());
    return "form";
  }

  @PostMapping("/form")
  public String sumbitForm(@ModelAttribute GitHubForm gitHubForm, Model model)
      throws JsonProcessingException {
    model.addAttribute("githubForm", gitHubForm);
    List<String> repositories = gitHubService.getRepositoriesWithHelloReadme(gitHubForm.getOrganization(),gitHubForm.getAccessToken());
    repositories.forEach(System.out::println);
    model.addAttribute("repositories", repositories);
    return "answer_page";
  }



  @PostMapping("/reps")
  public String getRepositories(GitHubForm githubForm, Model model, String organization) {
    // Здесь вы можете обрабатывать введенные данные и выполнять соответствующие действия
    // Например, вызывать метод сервиса для получения репозиториев и передавать результат в модель

    return "redirect:/form"; // Перенаправляем пользователя обратно к форме
  }
}

