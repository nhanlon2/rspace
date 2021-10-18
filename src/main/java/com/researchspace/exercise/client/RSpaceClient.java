package com.researchspace.exercise.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RSpaceClient {
    private static final String RSpaceBaseUrl = "";
    private RestTemplate restTemplate;
    public RSpaceClient(RestTemplateBuilder builder){
        restTemplate = builder.build();
    }

}
