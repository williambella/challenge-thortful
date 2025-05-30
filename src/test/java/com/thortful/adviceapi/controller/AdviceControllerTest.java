package com.thortful.adviceapi.controller;

import com.thortful.adviceapi.dto.AdviceSlip;
import com.thortful.adviceapi.exception.ExternalApiException;
import com.thortful.adviceapi.service.AdviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = AdviceController.class)
class AdviceControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private AdviceService adviceService;

    @Test
    void shouldReturnAdviceWhenServiceReturnsData() {
        AdviceSlip advice = new AdviceSlip(165, "Eliminate the unnecessary.");
        when(adviceService.getRandomAdvice()).thenReturn(Mono.just(advice));

        webTestClient.get()
                .uri("/advice/random")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(165)
                .jsonPath("$.advice").isEqualTo("Eliminate the unnecessary.");
    }

    @Test
    void shouldReturn503WhenExternalApiException() {
        when(adviceService.getRandomAdvice())
                .thenReturn(Mono.error(new ExternalApiException("API unavailable")));

        webTestClient.get()
                .uri("/advice/random")
                .exchange()
                .expectStatus().isEqualTo(503);
    }
}