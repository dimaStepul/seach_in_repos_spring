package org.example.spring_task;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class ClientErrorHandler {


  @ExceptionHandler(HttpClientErrorException.class)
  public ModelAndView handleHttpClientErrorException(HttpClientErrorException e) {
    HttpStatusCode status = e.getStatusCode();
    String returnPage;
    if (status == HttpStatus.BAD_REQUEST) {
      returnPage = "general_error_page";
    } else if (status == HttpStatus.UNAUTHORIZED) {
      returnPage = "unauthorized_error";
    } else if (status == HttpStatus.NOT_FOUND) {
      returnPage = "not_found_error_page";
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      returnPage = "general_error_page";
    }
    ModelAndView modelAndView = new ModelAndView(returnPage);
    modelAndView.setStatus(status);
    modelAndView.addObject("errorCode", status);
    modelAndView.addObject("errorMessage", e.getMessage());
    return modelAndView;
  }

}
