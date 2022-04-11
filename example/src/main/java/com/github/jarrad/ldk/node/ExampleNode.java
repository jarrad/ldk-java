package com.github.jarrad.ldk.node;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class ExampleNode {

  public static void main(String[] args) {
    SpringApplication.run(ExampleNode.class, args);
  }

  @Configuration
  static class ExampleNodeConfig {

  }

}
