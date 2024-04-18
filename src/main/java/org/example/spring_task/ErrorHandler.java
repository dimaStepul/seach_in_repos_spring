package org.example.spring_task;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.client.HttpClientErrorException;

public enum ErrorHandler {
  JSON_PROCESSING_EXCEPTION(JsonProcessingException.class, "general_error_page"),
  NOT_FOUND_EXCEPTION(HttpClientErrorException.NotFound.class, "not_found_error_page"),
  BAD_REQUEST_EXCEPTION(HttpClientErrorException.BadRequest.class, "general_error_page"),
  UNAUTHORIZED_EXCEPTION(HttpClientErrorException.Unauthorized.class, "unauthorized_error.html"),
  GENERAL_EXCEPTION(Exception.class, "general_error_page");

  private final Class<? extends Throwable> exceptionClass;
  private final String errorPage;


  ErrorHandler(Class<? extends Throwable> exceptionClass, String errorPage) {
    this.exceptionClass = exceptionClass;
    this.errorPage = errorPage;
  }

  public static ErrorHandler getErrorType(Throwable throwable) {
    for (ErrorHandler errorHandler : ErrorHandler.values()) {
      if (errorHandler.exceptionClass.isInstance(throwable)) {
        return errorHandler;
      }
    }
    return GENERAL_EXCEPTION;
  }

  public String getErrorPage() {
    return errorPage;
  }
}
