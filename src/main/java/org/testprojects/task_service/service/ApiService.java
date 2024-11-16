package org.testprojects.task_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class ApiService {

    private final WebClient webClient;

    public ApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.restful-api.dev").build();
    }

    public void fetchAndLogData() {
        try {
            String response = webClient.get()
                    .uri("/objects")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            log.info("Response: {}", response);
        } catch (Exception e) {
            log.error("Failed to fetch data from API: {}", e.getMessage());
        }
    }
}
