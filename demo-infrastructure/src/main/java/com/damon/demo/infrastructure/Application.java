package com.damon.demo.infrastructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.damon")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
