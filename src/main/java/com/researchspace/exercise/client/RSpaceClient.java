package com.researchspace.exercise.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.researchspace.exercise.client.exception.BadRequestException;
import com.researchspace.exercise.client.exception.SampleNotFoundException;
import com.researchspace.exercise.resource.SampleDetails;
import com.researchspace.exercise.resource.SampleResponse;
import com.researchspace.exercise.resource.SampleSummary;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@JsonComponent
public class RSpaceClient {
    private static final String RSPACE_BASE_URL = "https://demos.researchspace.com/api/inventory/v1/samples";
    private RestTemplate restTemplate;

    public RSpaceClient(RestTemplateBuilder builder) {
        restTemplate = builder
                .defaultHeader("apiKey", "X3m7dbnhC443VMdJJKpaB1rnqjdgmrGg")
                .build();
    }

    public JsonNode getSamples() {
        return restTemplate.getForObject(RSPACE_BASE_URL, JsonNode.class);
    }

    public SampleDetails getSampleByID(String id) {
        try {
            SampleDetails sampleDetails = restTemplate.getForObject(RSPACE_BASE_URL + "/" + id, SampleDetails.class);
            sampleDetails.calculateLocation();
            return sampleDetails;
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new SampleNotFoundException(id);
            } else {
                throw new BadRequestException(e);
            }
        }
    }

    public List<SampleSummary> getSampleSummaries() throws JsonProcessingException {
        SampleResponse response = restTemplate.getForObject(RSPACE_BASE_URL, SampleResponse.class);
        return response.getSamples();
    }

    public List<SampleSummary> getSampleSummariesExpiringWithin(int days) throws JsonProcessingException {
        SampleResponse response = restTemplate.getForObject(RSPACE_BASE_URL, SampleResponse.class);
        List<SampleSummary> summaries = response.getSamples();
        return summaries.stream().filter(summary -> {
                    if (summary.getExpiryDate() == null) {
                        return false;
                    }
                    LocalDate expiryDate = LocalDate.parse(summary.getExpiryDate());
                    long diff = ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
                    return diff < days; // TODO should this be <= ?
                }
        ).collect(Collectors.toList());
    }

}
