package com.duoc.seguridadcalidad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class BackendService {

    private static final Logger log = LoggerFactory.getLogger(BackendService.class);

    private final RestTemplate restTemplate;
    private final String backendBaseUrl;

    public BackendService(RestTemplate restTemplate, @Value("${backend.base-url}") String backendBaseUrl) {
        this.restTemplate = restTemplate;
        this.backendBaseUrl = backendBaseUrl;
    }

    public AuthResponse login(AuthRequest authRequest) {
        log.debug("-> BackendService.login payload={}", authRequest);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(backendBaseUrl + "/login", authRequest, String.class);
            log.debug("Backend login response status={} body={}", response.getStatusCode(), response.getBody());
            String token = response.getBody();
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            log.debug("<- BackendService.login token={}", token);
            return new AuthResponse(token);
        } catch (HttpStatusCodeException ex) {
            log.error("Backend login failed status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        }
    }

    public List<Map<String, Object>> getPatients(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        log.debug("-> BackendService.getPatients Authorization={}", jwtToken);
        try {
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    backendBaseUrl + "/patients",
                    HttpMethod.GET,
                    entity,
                    Map[].class
            );
            log.debug("<- BackendService.getPatients status={} body={}", response.getStatusCode(), Arrays.toString(response.getBody()));
            if (response.getBody() == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(response.getBody());
        } catch (HttpStatusCodeException ex) {
            log.error("Backend getPatients failed status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        }
    }

    public Map<String, Object> createPatient(String jwtToken, Map<String, Object> patient) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(patient, headers);

        log.debug("-> BackendService.createPatient Authorization={} patient={}", jwtToken, patient);
        try {
            Map response = restTemplate.postForObject(backendBaseUrl + "/patients", entity, Map.class);
            log.debug("<- BackendService.createPatient response={}", response);
            return response;
        } catch (HttpStatusCodeException ex) {
            log.error("Backend createPatient failed status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        }
    }

public List<Map<String, Object>> getAppointments(String jwtToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);        HttpEntity<Void> entity = new HttpEntity<>(headers);
        log.debug("-> BackendService.getAppointments Authorization={}", jwtToken);
        try {
            ResponseEntity<Map[]> response = restTemplate.exchange(
                    backendBaseUrl + "/appointments",
                    HttpMethod.GET,
                    entity,
                    Map[].class
            );
            log.debug("<- BackendService.getAppointments status={} body={}", response.getStatusCode(), Arrays.toString(response.getBody()));
            if (response.getBody() == null) {
                return Collections.emptyList();
            }
            return Arrays.asList(response.getBody());
        } catch (HttpStatusCodeException ex) {
            log.error("Backend getAppointments failed status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        }
    }

    public Map<String, Object> createAppointment(String jwtToken, Map<String, Object> appointment) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtToken);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(appointment, headers);

        log.debug("-> BackendService.createAppointment Authorization=Bearer {} appointment={}", jwtToken, appointment);
        try {
            Map response = restTemplate.postForObject(backendBaseUrl + "/appointments", entity, Map.class);
            log.debug("<- BackendService.createAppointment response={}", response);
            return response;
        } catch (HttpStatusCodeException ex) {
            log.error("Backend createAppointment failed status={} body={}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw ex;
        }
    }
}
