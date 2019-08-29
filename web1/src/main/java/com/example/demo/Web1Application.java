package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

//@EnableApolloConfig
@SpringBootApplication
public class Web1Application {

    public static void main(String[] args) {
        SpringApplication.run(Web1Application.class, args);
    }

}
//@EnableApolloConfig
//@SpringBootApplication
//public class Web1Application extends SpringBootServletInitializer {
//
//    public static void main(String[] args) {
//        SpringApplication.run(Web1Application.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(Web1Application.class);
//    }
//}
