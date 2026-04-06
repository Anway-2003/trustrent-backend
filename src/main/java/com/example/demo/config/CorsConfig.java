package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // सर्व API ला परवानगी देतो
                        .allowedOrigins("http://localhost:3000", "https://trustrent-frontend.vercel.app") 
                        // 👇 इथे आपण PATCH ॲड केलं आहे 👇
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH") 
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}