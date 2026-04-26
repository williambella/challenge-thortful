package com.thortful.adviceapi.service;

import com.thortful.adviceapi.dto.AdviceSlip;
import com.thortful.adviceapi.dto.SlipWrapper;
import com.thortful.adviceapi.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class AdviceServiceImpl implements AdviceService {

    private static final Logger log = LoggerFactory.getLogger(AdviceServiceImpl.class);
    private final WebClient webClient;

    public AdviceServiceImpl(@Value("${external.api}") String baseUrl, WebClient.Builder webClientBuilder) {
        log.info("Initializing AdviceService with baseUrl: {}", baseUrl);
        this.webClient = webClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    @Override
    public Mono<AdviceSlip> getRandomAdvice() {
        log.info("Starting request to external API");

        return webClient.get()
                .uri("/advice")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("Raw response from API: {}", response))
                .flatMap(this::parseToSlipWrapper)
                .map(SlipWrapper::slip)
                .doOnNext(advice -> log.info("Successfully parsed advice: {}", advice))
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("Error occurred: {}", error.getMessage(), error))
                .onErrorMap(Exception.class, ex -> {
                    log.error("Mapping error to ExternalApiException: {}", ex.getMessage());
                    return new ExternalApiException("Failed to fetch advice from external API", ex);
                });
    }

    private Mono<SlipWrapper> parseToSlipWrapper(String jsonResponse) {
        try {
            log.debug("Attempting to parse JSON: {}", jsonResponse);

            if (jsonResponse == null || jsonResponse.trim().isEmpty()) {
                log.error("Received null or empty response from external API");
                return Mono.error(new RuntimeException("External API returned empty response"));
            }

            if (!jsonResponse.trim().startsWith("{")) {
                throw new RuntimeException("Response is not JSON: " + jsonResponse);
            }

            // Parse ID
            int idStart = jsonResponse.indexOf("\"id\":") + 5;
            int idEnd = jsonResponse.indexOf(",", idStart);
            if (idEnd == -1) idEnd = jsonResponse.indexOf("}", idStart);
            String idStr = jsonResponse.substring(idStart, idEnd).trim();
            int id = Integer.parseInt(idStr);

            // Parse advice
            String advicePattern = "\"advice\":";
            int adviceKeyStart = jsonResponse.indexOf(advicePattern);
            int adviceValueStart = jsonResponse.indexOf("\"", adviceKeyStart + advicePattern.length()) + 1;
            int adviceValueEnd = jsonResponse.indexOf("\"", adviceValueStart);

            String advice = jsonResponse.substring(adviceValueStart, adviceValueEnd);

            AdviceSlip adviceSlip = new AdviceSlip(id, advice);
            SlipWrapper wrapper = new SlipWrapper(adviceSlip);

            log.debug("Successfully parsed: {}", wrapper);
            return Mono.just(wrapper);

        } catch (Exception e) {
            log.error("Failed to parse JSON response: {}", e.getMessage());
            return Mono.error(new RuntimeException("Failed to parse API response", e));
        }
    }
}
