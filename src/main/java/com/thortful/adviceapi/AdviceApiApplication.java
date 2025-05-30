package com.thortful.adviceapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Advice API", version = "1.0", description = "Return random advices"))
@SpringBootApplication
public class AdviceApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(AdviceApiApplication.class, args);
	}

}