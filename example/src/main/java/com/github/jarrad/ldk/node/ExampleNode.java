package com.github.jarrad.ldk.node;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.*;

@SpringBootApplication
public class ExampleNode {

  public static void main(String[] args) {
    SpringApplication.run(ExampleNode.class, args);
  }

  @Configuration
  static class ExampleNodeConfig {}
}
