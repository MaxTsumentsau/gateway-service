package com.max2ba.gateway_service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.serverError;

class FallbackTest extends BaseGatewayTest {

     @Test
     void shouldReturnFallbackWhenNotificationServiceReturns500() {
          wireMock.stubFor(post("/api/notifications/send")
                  .willReturn(serverError()));

          client.post().uri("/api/notifications/send")
                  .exchange()
                  .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                  .expectBody()
                  .jsonPath("$.code").isEqualTo("SERVICE_UNAVAILABLE")
                  .jsonPath("$.details").isEqualTo("Ошибка в сервисе уведомлений");
     }

     @Test
     void shouldReturnFallbackWhenUserServiceReturns500() {
          wireMock.stubFor(post("/api/users")
                  .willReturn(serverError()));

          client.post()
                  .uri("/api/users")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue("""
                {
                  "name": "Max 2ba",
                  "email": "max@gmail.com",
                  "age": 34
                }
            """)
                  .exchange()
                  .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                  .expectBody()
                  .jsonPath("$.code").isEqualTo("SERVICE_UNAVAILABLE")
                  .jsonPath("$.details").isEqualTo("Ошибка в сервисе пользователей");
     }

}

