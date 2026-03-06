package com.max2ba.gateway_service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.config.import=optional:none",
                "spring.cloud.config.enabled=false",
                "spring.cloud.config.import-check.enabled=false"
        }
)
@ActiveProfiles("test")
public abstract class BaseGatewayTest {

     @LocalServerPort
     protected int port;

     protected WebTestClient client;

     protected static WireMockServer wireMock;

     @BeforeAll
     static void setupWireMock() {
          wireMock = new WireMockServer(9999);
          wireMock.start();
     }

     @AfterAll
     static void stopWireMock() {
          wireMock.stop();
     }

     @BeforeEach
     void setupClient() {
          client = WebTestClient.bindToServer()
                  .baseUrl("http://localhost:" + port)
                  .responseTimeout(Duration.ofSeconds(5))
                  .build();

          wireMock.resetAll();
     }
}
