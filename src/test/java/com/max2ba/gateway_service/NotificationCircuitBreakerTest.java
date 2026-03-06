package com.max2ba.gateway_service;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class NotificationCircuitBreakerTest extends BaseGatewayTest {

     @Test
     void circuitBreakerShouldOpenAfterSingleFailure() {
          wireMock.stubFor(post("/api/notifications/send")
                  .willReturn(serverError()));

          client.post().uri("/api/notifications/send")
                  .exchange()
                  .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

          client.post().uri("/api/notifications/send")
                  .exchange()
                  .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

          wireMock.verify(1, postRequestedFor(urlEqualTo("/api/notifications/send")));
     }

     @Test
     void circuitBreakerShouldOpenAfterEnoughFailures() {
          //у меня другие настройки СБ для юзер-сервиса
          wireMock.stubFor(post("/api/users")
                  .willReturn(serverError()));

          for (int i = 0; i < 5; i++) {
               client.post()
                       .uri("/api/users")
                       .contentType(MediaType.APPLICATION_JSON)
                       .bodyValue("""
                                   {
                                     "name": "Max",
                                     "email": "max@gmail.com",
                                     "age": 34
                                   }
                               """)
                       .exchange()
                       .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
          }

          client.post()
                  .uri("/api/users")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue("""
                              {
                                "name": "Max",
                                "email": "max@gmail.com",
                                "age": 34
                              }
                          """)
                  .exchange()
                  .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

          wireMock.verify(5, postRequestedFor(urlEqualTo("/api/users")));
     }
}

