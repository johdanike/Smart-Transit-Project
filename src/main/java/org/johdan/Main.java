package org.johdan;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "org.johdan.user.data.models")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}