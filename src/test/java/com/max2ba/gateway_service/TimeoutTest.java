package com.max2ba.gateway_service;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class TimeoutTest extends BaseGatewayTest {

     @Test
     void shouldReturnFallbackOnResponseTimeout() {
          wireMock.stubFor(post("/api/notifications/send")
                  .willReturn(aResponse().withFixedDelay(5000)));

          WebTestClient localClient = WebTestClient.bindToServer()
                  .baseUrl("http://localhost:" + port)
                  .responseTimeout(Duration.ofSeconds(10))
                  .build();

          localClient.post()
                  .uri("/api/notifications/send")
                  .exchange()
                  .expectStatus().isEqualTo(503)
                  .expectBody()
                  .jsonPath("$.code").isEqualTo("SERVICE_UNAVAILABLE")
                  .jsonPath("$.message").isEqualTo("Целевой сервис недоступен")
                  .jsonPath("details").isEqualTo("Ошибка в сервисе уведомлений");
     }
}
