//package com.ubedpathan.TodoApp.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**") // Apply CORS for all endpoints
//                        .allowedOrigins("http://localhost:2121") // Allow your frontend's origin
//                        .allowedMethods("OPTIONS","PUT", "DELETE","GET", "POST") // Allowed HTTP methods
//                        .allowedHeaders("*")
//                        .exposedHeaders("Authorization")// Allow any headers
//                        .allowCredentials(true); // Allow cookies and credentials
//            }
//        };
//    }
//}
//
