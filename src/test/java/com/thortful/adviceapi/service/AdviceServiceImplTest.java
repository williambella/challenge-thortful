package com.thortful.adviceapi.service;

import com.thortful.adviceapi.dto.AdviceSlip;
import com.thortful.adviceapi.exception.ExternalApiException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdviceServiceImplTest {

    private MockWebServer mockWebServer;
    private AdviceServiceImpl adviceService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = String.format("http://localhost:%s", mockWebServer.getPort());
        adviceService = new AdviceServiceImpl(baseUrl, WebClient.builder());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldReturnAdviceWhenApiReturnsValidJson() {
        String response = """
            {
                "slip": {
                    "id": 165,
                    "advice": "Eliminate the unnecessary."
                }
            }
            """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(response)
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(adviceService.getRandomAdvice())
                .assertNext(advice -> {
                    assertEquals(165, advice.id());
                    assertEquals("Eliminate the unnecessary.", advice.advice());
                })
                .verifyComplete();
    }

    @Test
    void shouldThrowExceptionWhenApiReturns500() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        StepVerifier.create(adviceService.getRandomAdvice())
                .expectError(ExternalApiException.class)
                .verify();
    }

    @Test
    void shouldThrowExceptionWhenInvalidJson() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("invalid json")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(adviceService.getRandomAdvice())
                .expectError(ExternalApiException.class)
                .verify();
    }
}