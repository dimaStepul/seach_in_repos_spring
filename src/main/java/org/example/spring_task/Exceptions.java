package org.example.spring_task;

public class Exceptions {

  public static class UnknownEncodingException extends RuntimeException {

    public UnknownEncodingException(String message) {
      super(message);
    }
  }
}
