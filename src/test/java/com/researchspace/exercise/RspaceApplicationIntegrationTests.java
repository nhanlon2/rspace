package com.researchspace.exercise;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.researchspace.exercise.client.RSpaceClient;
import com.researchspace.exercise.client.exception.SampleNotFoundException;
import com.researchspace.exercise.resource.SampleDetails;
import com.researchspace.exercise.resource.SampleSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest()
@AutoConfigureMockMvc
public class RspaceApplicationIntegrationTests {
    private final static String SAMPLE_ID = "1";
    private static final String EXPIRY_DAYS = "5";
    @Autowired
    private MockMvc mvc;
    @MockBean
    private RSpaceClient rSpaceClientMock;
    @Value(value = "classpath:samples.json")
    private Resource samplesJson;
    private List<SampleSummary> summaryList;
    private SampleDetails sampleDetails;


    @BeforeEach
    public void setUp() throws IOException {
        summaryList = List.of(new SampleSummary("name", "id", "desc", "expiry"));
        sampleDetails = new SampleDetails();
        sampleDetails.setName("name");
        Path toFilePath = Paths.get(samplesJson.getURI());
        String samples = Files.readString(toFilePath);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode samplesjson = mapper.readTree(samples);
        when(rSpaceClientMock.getSamples()).thenReturn(samplesjson);
        when(rSpaceClientMock.getSampleSummaries()).thenReturn(summaryList);
        when(rSpaceClientMock.getSampleSummariesExpiringWithin(eq(5))).thenReturn(summaryList);
        when(rSpaceClientMock.getSampleByID(eq(SAMPLE_ID))).thenReturn(sampleDetails);
    }

    @Test
    public void testGetSamples() throws Exception {
        MvcResult result = mvc.perform(get("/samples")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertThat(content, containsString("samples"));
    }

    @Test
    public void testGetSampleSummaries() throws Exception {
        MvcResult result = mvc.perform(get("/sampleSummaries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        SampleSummary[] summaries = mapper.readValue(result.getResponse().getContentAsString(), SampleSummary[].class);
        assertEquals("id", summaries[0].getId());
    }

    @Test
    public void testGetSampleSummariesExpiringWithin() throws Exception {
        MvcResult result = mvc.perform(get("/sampleSummaries?expiresInLessThan=" + EXPIRY_DAYS)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        SampleSummary[] summaries = mapper.readValue(result.getResponse().getContentAsString(), SampleSummary[].class);
        assertEquals("id", summaries[0].getId());
    }

    @Test
    public void testGetSampleById() throws Exception {
        MvcResult result = mvc.perform(get("/sampleDetails/" + SAMPLE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        SampleDetails details = mapper.readValue(result.getResponse().getContentAsString(), SampleDetails.class);
        assertEquals("name", details.getName());
    }

    @Test
    public void testHandlesGenricException() throws Exception {
        String rootCause = "bad stuff that should not be made public";
        doThrow(new RuntimeException(rootCause)).when(rSpaceClientMock).getSamples();
        MvcResult result = mvc.perform(get("/samples")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertThat(content, containsString("There is a problem with your request, please contact support"));
        assertThat(content, not(containsString(rootCause)));
    }

    @Test
    public void testHandlesSampleNotFoundException() throws Exception {
        SampleNotFoundException err = new SampleNotFoundException(SAMPLE_ID);
        doThrow(err).when(rSpaceClientMock).getSampleByID(SAMPLE_ID);
        MvcResult result = mvc.perform(get("/sampleDetails/" + SAMPLE_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        assertThat(content, containsString("Unable to find the sample with ID: " + SAMPLE_ID));
    }

}
