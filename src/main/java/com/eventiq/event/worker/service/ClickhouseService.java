package com.eventiq.event.worker.service;

import com.eventiq.shared.dto.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.Map;

@Service
public class ClickhouseService {

    private final String clickhouseUrl = "http://localhost:8123/";

    @Autowired
    private RestTemplate restTemplate;

    public void insertSession(Map<String, Object> metaData, Event event) {
        String query = "INSERT INTO sessions FORMAT JSONEachRow";

        String json = String.format(
                "{\"projectId\":\"%s\", \"eventTimeStamp\":%d, \"ip\":\"%s\", \"country\":\"%s\", \"city\":\"%s\", \"userAgent\":\"%s\", \"agentType\":\"%s\"}\n",
                event.getProjectId(),
                LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                metaData.get("ip").toString(),
                metaData.get("country").toString(),
                metaData.get("location").toString(),
                metaData.get("browser").toString(),
                metaData.get("browser").toString()
        );

        String fullUrl = clickhouseUrl + "?query=" + query; // query is plain text, not encoded

        String username = "default";
        String password = "default";

        String auth = username + ":" + password;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + encodedAuth;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set("Authorization", authHeader);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        restTemplate.exchange(
                fullUrl,
                HttpMethod.POST,
                request,
                String.class
        );
    }

}
