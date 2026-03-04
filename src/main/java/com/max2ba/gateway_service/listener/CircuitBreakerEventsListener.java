package com.max2ba.gateway_service.listener;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CircuitBreakerEventsListener {
     public CircuitBreakerEventsListener(CircuitBreakerRegistry registry) {
          registry.getAllCircuitBreakers().forEach(cb -> {
               cb.getEventPublisher()
                       .onStateTransition(event -> {
                            log.warn("CircuitBreaker '{}' state changed: {} -> {}",
                                    cb.getName(),
                                    event.getStateTransition().getFromState(),
                                    event.getStateTransition().getToState());
                       })
                       .onError(event -> {
                            log.error("CircuitBreaker '{}' error: {}",
                                    cb.getName(),
                                    event.getThrowable().getMessage());
                       })
                       .onSuccess(event -> {
                            log.info("CircuitBreaker '{}' success", cb.getName());
                       });
          });
     }
}