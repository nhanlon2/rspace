package com.researchspace.exercise.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RSpaceClient {
    private static final String RSPACE_BASE_URL = "https://demos.researchspace.com/api/inventory/v1/samples?pageNumber=0&pageSize=20";
    private RestTemplate restTemplate;
    public RSpaceClient(RestTemplateBuilder builder){
        restTemplate = builder
                .defaultHeader("apiKey", "X3m7dbnhC443VMdJJKpaB1rnqjdgmrGg")
                .build();
    }

    public JsonNode getSamples () {
        return restTemplate.getForObject(RSPACE_BASE_URL, JsonNode.class);
    }

}
