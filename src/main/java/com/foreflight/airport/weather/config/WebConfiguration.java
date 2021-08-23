package com.foreflight.airport.weather.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfiguration {

    @Bean
    public RestTemplate foreflightClient(
            @Value("${foreflight-api.airport.credentials.username}") String username,
            @Value("${foreflight-api.airport.credentials.password}") String password,
            @Value("${foreflight-api.base-url}") String url) {

        return new RestTemplateBuilder()
            .basicAuthentication(username, password)
            .defaultHeader("ff-coding-exercise", "1")
            .rootUri(url)
            .build();

    }

}
