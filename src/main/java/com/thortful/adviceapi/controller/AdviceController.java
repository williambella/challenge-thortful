package com.thortful.adviceapi.controller;

import com.thortful.adviceapi.dto.AdviceSlip;
import com.thortful.adviceapi.service.AdviceService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/advice")
public class AdviceController {

    private final AdviceService adviceService;

    public AdviceController(AdviceService adviceService) {
        this.adviceService = adviceService;
    }

    @Operation(summary = "Get a random advice")
    @GetMapping("/random")
    public Mono<AdviceSlip> getRandomAdvice() {
        return adviceService.getRandomAdvice();
    }
}
