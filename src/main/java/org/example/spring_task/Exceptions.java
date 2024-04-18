package org.example.spring_task;

public class Exceptions {

  public static class UnknownEncodingException extends RuntimeException {

    public UnknownEncodingException(String message) {
      super(message);
    }

    public static class InvalidJsonDataException extends RuntimeException {

      public InvalidJsonDataException(String message) {
        super(message);
      }
    }

    public static class EmptyRepositoryListException extends RuntimeException {

      public EmptyRepositoryListException() {
        super("The list of repositories is empty");
      }
    }


  }
}
