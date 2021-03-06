package com.researchspace.exercise.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.researchspace.exercise.client.RSpaceClient;
import com.researchspace.exercise.resource.SampleDetails;
import com.researchspace.exercise.resource.SampleSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class RSpaceSamplesController {

    private final RSpaceClient client;

    public RSpaceSamplesController(RSpaceClient client) {
        this.client = client;
    }

    @GetMapping("/samples")
    public ResponseEntity<JsonNode> index() {

        return ok(client.getSamples());
    }

    @GetMapping("/sampleSummaries")
    public ResponseEntity<List<SampleSummary>> summaries(@RequestParam(name = "expiresInLessThan", required = false) Integer days) throws JsonProcessingException {
        if (days != null) {
            return ok(client.getSampleSummariesExpiringWithin(days));
        }
        return ok(client.getSampleSummaries());
    }

    @GetMapping("/sampleDetails/{id}")
    public ResponseEntity<SampleDetails> sampleDetails(@PathVariable String id) throws JsonProcessingException {

        return ok(client.getSampleByID(id));
    }
}
