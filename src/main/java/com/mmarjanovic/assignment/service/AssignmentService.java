package com.mmarjanovic.assignment.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmarjanovic.assignment.constants.Constants;
import com.mmarjanovic.assignment.dto.Assignment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class AssignmentService {

    private RestTemplate restTemplate = new RestTemplate();

    public List<Assignment> getAssignments() {
        return fetchAssignments();
    }

    private List<Assignment> fetchAssignments() {
        List<Assignment> assignmentsList = null;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        ResponseEntity<String> exchange =
            restTemplate.exchange(Constants.ASSIGNMENTS_URL, HttpMethod.GET, entity, String.class);

        if (exchange != null && exchange.getStatusCode() != null && exchange.getStatusCode().is2xxSuccessful()) {
            try {
                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                assignmentsList = objectMapper != null ?
                    objectMapper.readValue(exchange.getBody(), objectMapper.getTypeFactory().constructCollectionType(List.class, Assignment.class)) : null;
            }
            catch (IOException e) {
                System.out.println("Error occurred while converting response to assignments. " + e.getMessage());
            }
        }

        return assignmentsList;
    }
}