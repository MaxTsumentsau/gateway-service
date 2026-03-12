package com.max2ba.gateway_service;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

class RoutingTest extends BaseGatewayTest {

     @Test
     void shouldRouteToNotificationService() {
          wireMock.stubFor(post("/api/notifications/send")
                  .withRequestBody(equalToJson("""
                {
                  "userOperation": "CREATE",
                  "email": "max@gmail.com"
                }
            """))
                  .willReturn(okJson("""
                {
                  "code": "SUCCESS",
                  "message": "Сообщение успешно отправлено",
                  "details": "CREATE: max@gmail.com"
                }
            """)));

          client.post()
                  .uri("/api/notifications/send")
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue("""
                {
                  "userOperation": "CREATE",
                  "email": "max@gmail.com"
                }
            """)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.code").isEqualTo("SUCCESS")
                  .jsonPath("$.message").isEqualTo("Сообщение успешно отправлено")
                  .jsonPath("$.details").isEqualTo("CREATE: max@gmail.com");
     }

     @Test
     void shouldRouteToUserService() {
          wireMock.stubFor(post("/api/users")
                  .withRequestBody(equalToJson("""
                {
                  "name": "Max 2ba",
                  "email": "max@gmail.com",
                  "age": 34
                }
            """))
                  .willReturn(okJson("""
                {
                  "code": "SUCCESS",
                  "message": "Сообщение успешно отправлено",
                  "data": {
                    "id": "11111111-2222-3333-4444-555555555555",
                    "name": "Max 2ba",
                    "email": "max@gmail.com",
                    "age": 34
                  }
                }
            """)));

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
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.code").isEqualTo("SUCCESS")
                  .jsonPath("$.message").isEqualTo("Сообщение успешно отправлено")
                  .jsonPath("$.data.id").isEqualTo("11111111-2222-3333-4444-555555555555")
                  .jsonPath("$.data.name").isEqualTo("Max 2ba")
                  .jsonPath("$.data.email").isEqualTo("max@gmail.com")
                  .jsonPath("$.data.age").isEqualTo(34);
     }



     @Test
     void shouldRouteUpdateUser() {
          UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");

          wireMock.stubFor(put("/api/users/" + id)
                  .withRequestBody(equalToJson("""
                {
                  "name": "Max Tumensev",
                  "email": "max@gmail.com",
                  "age": 21
                }
            """))
                  .willReturn(okJson("""
                {
                  "code": "SUCCESS",
                  "message": "Сообщение успешно отправлено",
                  "data": {
                    "id": "11111111-2222-3333-4444-555555555555",
                    "name": "Max Tumensev",
                    "email": "max@gmail.com",
                    "age": 21
                  }
                }
            """)));

          client.put()
                  .uri("/api/users/" + id)
                  .contentType(MediaType.APPLICATION_JSON)
                  .bodyValue("""
                {
                  "name": "Max Tumensev",
                  "email": "max@gmail.com",
                  "age": 21
                }
            """)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.name").isEqualTo("Max Tumensev")
                  .jsonPath("$.data.age").isEqualTo(21);
     }

     @Test
     void shouldRouteGetUser() {
          UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");

          wireMock.stubFor(get("/api/users/" + id)
                  .willReturn(okJson("""
                {
                  "code": "SUCCESS",
                  "message": "Сообщение успешно отправлено",
                  "data": {
                    "id": "11111111-2222-3333-4444-555555555555",
                    "name": "Max 2ba",
                    "email": "max@gmail.com",
                    "age": 34
                  }
                }
            """)));

          client.get()
                  .uri("/api/users/" + id)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.id").isEqualTo(id.toString())
                  .jsonPath("$.data.name").isEqualTo("Max 2ba")
                  .jsonPath("$.data.email").isEqualTo("max@gmail.com")
                  .jsonPath("$.data.age").isEqualTo(34);
     }

     @Test
     void shouldRouteSearchUsers() {
          wireMock.stubFor(get(urlPathEqualTo("/api/users/search"))
                  .withQueryParam("name", equalTo("Max"))
                  .withQueryParam("page", equalTo("0"))
                  .withQueryParam("size", equalTo("5"))
                  .willReturn(okJson("""
                {
                  "code": "SUCCESS",
                  "message": "Сообщение успешно отправлено",
                  "data": {
                    "content": [
                      {
                        "id": "11111111-2222-3333-4444-555555555555",
                        "name": "Max 2ba",
                        "email": "max@gmail.com",
                        "age": 34
                      }
                    ],
                    "page": 0,
                    "size": 5,
                    "totalElements": 1
                  }
                }
            """)));

          client.get()
                  .uri(uriBuilder -> uriBuilder
                          .path("/api/users/search")
                          .queryParam("name", "Max")
                          .queryParam("page", 0)
                          .queryParam("size", 5)
                          .build())
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.content[0].name").isEqualTo("Max 2ba")
                  .jsonPath("$.data.content[0].email").isEqualTo("max@gmail.com")
                  .jsonPath("$.data.content[0].age").isEqualTo(34);
     }

     @Test
     void shouldRouteDeleteUser() {
          UUID id = UUID.fromString("11111111-2222-3333-4444-555555555555");

          wireMock.stubFor(delete("/api/users/" + id)
                  .willReturn(okJson("""
                {
                  "code": "SUCCESS",
                  "message": "Сообщение успешно отправлено",
                  "data": {
                    "id": "11111111-2222-3333-4444-555555555555",
                    "name": "Max 2ba",
                    "email": "max@gmail.com",
                    "age": 34
                  }
                }
            """)));

          client.delete()
                  .uri("/api/users/" + id)
                  .exchange()
                  .expectStatus().isOk()
                  .expectBody()
                  .jsonPath("$.data.id").isEqualTo(id.toString())
                  .jsonPath("$.data.name").isEqualTo("Max 2ba")
                  .jsonPath("$.data.email").isEqualTo("max@gmail.com")
                  .jsonPath("$.data.age").isEqualTo(34);
     }
}