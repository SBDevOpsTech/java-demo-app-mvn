package com.example.app2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class App1Application {

	public static void main(String[] args) {
		SpringApplication.run(App1Application.class, args);
	}

}

// REST Controller to expose a web endpoint
@RestController
@RequestMapping("/api")
class HelloController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, Welcome to Spring Boot Web App!";
    }
}