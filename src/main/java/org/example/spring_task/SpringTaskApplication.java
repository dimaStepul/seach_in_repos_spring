package org.example.spring_task;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
@EnableCaching
public class SpringTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringTaskApplication.class, args);
  }

}



