package com.blps.blps;

import io.camunda.client.annotation.Deployment;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@Deployment(resources = "classpath:processes/diagram_1.bpmn")
public class BlpsApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(BlpsApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BlpsApplication.class, args);
    }
}
