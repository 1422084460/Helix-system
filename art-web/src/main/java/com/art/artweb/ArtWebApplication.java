package com.art.artweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.art"})
public class ArtWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtWebApplication.class, args);
    }

}
