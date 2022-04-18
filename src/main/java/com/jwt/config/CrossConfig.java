package com.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class CrossConfig {

 @Bean
	public WebMvcConfigurer getCrossConfiguration() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
				        .allowedOrigins(" http://localhost:4200")
				        .allowedMethods("POST","GET","PUT","DELETE","PACH")
				        .allowedHeaders("*")
				        .exposedHeaders("Authorization")
				        .allowCredentials(true)
				        .maxAge(3600) ;
			}
		}; 
	}
}
