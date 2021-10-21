package com.researchspace.exercise.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.researchspace.exercise.client.exception.SampleNotFoundException;
import com.researchspace.exercise.resource.SampleDetails;
import com.researchspace.exercise.resource.SampleResponse;
import com.researchspace.exercise.resource.SampleSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
public class RSpaceClientTest {

    private static final String RSPACE_BASE_URL = "https://demos.researchspace.com/api/inventory/v1/samples";
    private static final String SAMPLE_ID = "5";
    @Autowired
    private RSpaceClient testee;

    @Autowired
    private MockRestServiceServer server;
    @Value(value = "classpath:samples.json")
    private Resource samplesJsonFile;
    @Value(value = "classpath:sampleByID.json")
    private Resource sampleByIDJsonFile;
    private String samplesJson;
    private String sampleDetailsJson;

    @BeforeEach
    public void setUp() throws IOException {
        Path toFilePath = Paths.get(samplesJsonFile.getURI());
        samplesJson = Files.readString(toFilePath);
        toFilePath = Paths.get(sampleByIDJsonFile.getURI());
        sampleDetailsJson = Files.readString(toFilePath);
    }

    @Test
    public void testGetSamples() {
        server.expect(requestTo(RSPACE_BASE_URL))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(samplesJson, MediaType.APPLICATION_JSON));
        JsonNode result = testee.getSamples();
        assertNotNull(result);
        server.verify();
    }

    @Test
    public void testGetSampleSummaries() throws JsonProcessingException {
        server.expect(requestTo(RSPACE_BASE_URL))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(samplesJson, MediaType.APPLICATION_JSON));
        List<SampleSummary> result = testee.getSampleSummaries();
        assertEquals(4, result.size());
        server.verify();
    }

    @Test
    public void testGetSampleSummariesExpiringWithin() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        SampleResponse response = mapper.readValue(samplesJson, SampleResponse.class);
        SampleSummary[] summaries = response.getSamples().toArray(new SampleSummary[4]);
        summaries[0].setExpiryDate(LocalDate.now().plusDays(4).toString());
        summaries[1].setExpiryDate(LocalDate.now().plusDays(7).toString());
        summaries[2].setExpiryDate(LocalDate.now().plusDays(8).toString());
        summaries[3].setExpiryDate(null);
        String summariesJson = mapper.writeValueAsString(response);
        server.expect(requestTo(RSPACE_BASE_URL))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(summariesJson, MediaType.APPLICATION_JSON));
        List<SampleSummary> result = testee.getSampleSummariesExpiringWithin(7);
        assertEquals(1, result.size());
        assertEquals("Antibody_expired", result.get(0).getName());
        server.verify();
    }

    @Test
    public void testGetSampleByID() {
        server.expect(requestTo(RSPACE_BASE_URL + "/" + SAMPLE_ID))
                .andExpect(method(HttpMethod.GET)).andRespond(withSuccess(sampleDetailsJson, MediaType.APPLICATION_JSON));
        SampleDetails sampleDetails = testee.getSampleByID(SAMPLE_ID);
        assertEquals("Antibody_expired", sampleDetails.getName());
        assertEquals(" Neils_Container  6560 4 1 WB nhanlon null WB nhanlon null", sampleDetails.getLocation());
    }

    @Test
    public void testGetSampleByIDNotFound() {
        server.expect(requestTo(RSPACE_BASE_URL + "/" + SAMPLE_ID))
                .andExpect(method(HttpMethod.GET)).andRespond(withStatus(HttpStatus.NOT_FOUND));
        SampleNotFoundException exception = assertThrows(SampleNotFoundException.class,
                () -> testee.getSampleByID(SAMPLE_ID));
        assertEquals("Unable to find the sample with ID: "+SAMPLE_ID, exception.getMessage());
    }

}
