package com.researchspace.exercise.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
@RestClientTest
public class RSpaceClientTest {

    private static final String RSPACE_BASE_URL = "https://demos.researchspace.com/api/inventory/v1/samples";
    @Autowired
    private RSpaceClient testee;

    @Autowired
    private MockRestServiceServer server;
    @Value(value = "classpath:samples.json")
    private Resource samplesJson;
    @Value(value = "classpath:sampleByID.json")
    private Resource sampleByIDJson;

//    @BeforeEach
//    public void setUp(){
//
//    }

    @Test
    public void testGetSamples() throws IOException {
        Path toFilePath = Paths.get(samplesJson.getURI());
        String samples = Files.readString(toFilePath);
        server.expect(requestTo(RSPACE_BASE_URL))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(samples, MediaType.APPLICATION_JSON));
        JsonNode result = testee.getSamples();
        assertNotNull(result);
    }

//    @Test
//    public void testGetSampleSummaries(){
//
//    }
//
//    @Test
//    public void testGetSampleByID(){
//
//    }
//
//    @Test
//    public void testGetSampleSummariesExpiringWithin(){
//
//    }
}
