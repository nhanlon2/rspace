package com.researchspace.exercise.rest;

import com.fasterxml.jackson.databind.JsonNode;
import com.researchspace.exercise.client.RSpaceClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class RSpaceSamplesController {

    private final RSpaceClient client;

    public RSpaceSamplesController(RSpaceClient client){
        this.client = client;
    }
    @GetMapping("/")
    public ResponseEntity<JsonNode> index() {

        return ok(client.getSamples());
    }
}
