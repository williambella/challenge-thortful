package com.thortful.adviceapi.service;

import com.thortful.adviceapi.dto.AdviceSlip;
import reactor.core.publisher.Mono;

public interface AdviceService {
    Mono<AdviceSlip> getRandomAdvice();
}
