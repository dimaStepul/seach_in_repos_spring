package org.example.spring_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ServerErrorHandler {

  @ExceptionHandler(JsonProcessingException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleJsonProcessingException(JsonProcessingException e, Model model) {
    model.addAttribute("errorCode", "500");
    model.addAttribute("errorMessage", e.getMessage());
    return "general_error_page";
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public String handleGeneralException(Exception e, Model model) {
    model.addAttribute("errorCode", "500");
    model.addAttribute("errorMessage", e.getMessage());
    return "general_error_page";
  }
}
