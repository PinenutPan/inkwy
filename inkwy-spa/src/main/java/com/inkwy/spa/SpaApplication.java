package com.inkwy.spa;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class SpaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpaApplication.class, args);
    }

}
