package com.max2ba.gateway_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max2ba.gateway_service.errorHandler.GatewayErrorHandler;
import io.netty.channel.ConnectTimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.net.UnknownHostException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class GatewayErrorHandlerTest {

     private GatewayErrorHandler handler;
     private ObjectMapper objectMapper;

     @BeforeEach
     void setup() {
          objectMapper = new ObjectMapper();
          handler = new GatewayErrorHandler(objectMapper);
     }

     @Test
     void testUnknownHostException() {
          MockServerWebExchange exchange = MockServerWebExchange.from(
                  MockServerHttpRequest.get("/test").build()
          );

          UnknownHostException ex = new UnknownHostException("Host not found");

          handler.handle(exchange, ex).block();

          assertEquals(HttpStatus.SERVICE_UNAVAILABLE, exchange.getResponse().getStatusCode());

          String body = exchange.getResponse().getBodyAsString().block();
          assertTrue(body.contains("\"code\":\"DNS_ERROR\""));
          assertTrue(body.contains("Host not found"));
     }

     @Test
     void testTimeoutException() {
          MockServerWebExchange exchange = MockServerWebExchange.from(
                  MockServerHttpRequest.get("/test").build()
          );

          ConnectTimeoutException ex = new ConnectTimeoutException("Timeout");

          handler.handle(exchange, ex).block();

          assertEquals(HttpStatus.GATEWAY_TIMEOUT, exchange.getResponse().getStatusCode());

          String body = exchange.getResponse().getBodyAsString().block();
          assertTrue(body.contains("\"code\":\"TIMEOUT\""));
     }

     @Test
     void testNoSuchElementException() {
          MockServerWebExchange exchange = MockServerWebExchange.from(
                  MockServerHttpRequest.get("/test").build()
          );

          NoSuchElementException ex = new NoSuchElementException("Not found");

          handler.handle(exchange, ex).block();

          assertEquals(HttpStatus.NOT_FOUND, exchange.getResponse().getStatusCode());

          String body = exchange.getResponse().getBodyAsString().block();
          assertTrue(body.contains("\"code\":\"ROUTE_NOT_FOUND\""));
     }

     @Test
     void testIllegalStateException() {
          MockServerWebExchange exchange = MockServerWebExchange.from(
                  MockServerHttpRequest.get("/test").build()
          );

          IllegalStateException ex = new IllegalStateException("Illegal state");

          handler.handle(exchange, ex).block();

          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());

          String body = exchange.getResponse().getBodyAsString().block();
          assertTrue(body.contains("\"code\":\"INTERNAL_ERROR\""));
     }

     @Test
     void testUnexpectedException() {
          MockServerWebExchange exchange = MockServerWebExchange.from(
                  MockServerHttpRequest.get("/test").build()
          );

          RuntimeException ex = new RuntimeException("Unexpected");

          handler.handle(exchange, ex).block();

          assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());

          String body = exchange.getResponse().getBodyAsString().block();
          assertTrue(body.contains("\"code\":\"UNEXPECTED_ERROR\""));
     }

     @Test
     void testSerializationError() throws Exception {
          ObjectMapper failingMapper = mock(ObjectMapper.class);
          when(failingMapper.writeValueAsBytes(any())).thenThrow(new RuntimeException("boom"));

          GatewayErrorHandler handler = new GatewayErrorHandler(failingMapper);

          MockServerWebExchange exchange = MockServerWebExchange.from(
                  MockServerHttpRequest.get("/test").build()
          );

          handler.handle(exchange, new RuntimeException("x")).block();

          String body = exchange.getResponse().getBodyAsString().block();
          assertEquals("Error: internal serialization error", body);
     }
}