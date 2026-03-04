package com.max2ba.gateway_service.errorHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max2ba.gateway_service.dto.ApiResponse;
import com.max2ba.gateway_service.dto.ResponseCode;
import io.netty.channel.ConnectTimeoutException;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

@Component
@Slf4j
@RequiredArgsConstructor
public class GatewayErrorHandler implements WebExceptionHandler {

     private final ObjectMapper objectMapper;

     @Override
     public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
          HttpStatus status = mapStatus(ex);
          ResponseCode code = mapCode(ex);

          ApiResponse body = ApiResponse.error(code, ex.getMessage());

          byte[] bytes;
          try {
               bytes = objectMapper.writeValueAsBytes(body);
          } catch (Exception e) {
               bytes = ("Error: internal serialization error").getBytes(StandardCharsets.UTF_8);
          }

          exchange.getResponse().setStatusCode(status);
          exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

          DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);

          log.error("Gateway error: in class {} with status: {}. {}",
                  ex.getClass().getSimpleName(), status, ex.getMessage());

          return exchange.getResponse().writeWith(Mono.just(buffer));
     }

     private HttpStatus mapStatus(Throwable ex) {
          if (ex instanceof UnknownHostException) return HttpStatus.SERVICE_UNAVAILABLE;
          if (ex instanceof ConnectTimeoutException) return HttpStatus.GATEWAY_TIMEOUT;
          if (ex instanceof ReadTimeoutException) return HttpStatus.GATEWAY_TIMEOUT;
          if (ex instanceof NoSuchElementException) return HttpStatus.NOT_FOUND;
          if (ex instanceof IllegalStateException) return HttpStatus.INTERNAL_SERVER_ERROR;
          return HttpStatus.INTERNAL_SERVER_ERROR;
     }

     private ResponseCode mapCode(Throwable ex) {
          if (ex instanceof UnknownHostException) return ResponseCode.DNS_ERROR;
          if (ex instanceof ConnectTimeoutException) return ResponseCode.TIMEOUT;
          if (ex instanceof ReadTimeoutException) return ResponseCode.TIMEOUT;
          if (ex instanceof NoSuchElementException) return ResponseCode.ROUTE_NOT_FOUND;
          if (ex instanceof IllegalStateException) return ResponseCode.INTERNAL_ERROR;
          return ResponseCode.UNEXPECTED_ERROR;
     }
}