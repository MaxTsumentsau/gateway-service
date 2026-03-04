package com.max2ba.gateway_service.controller;

import com.max2ba.gateway_service.dto.ApiResponse;
import com.max2ba.gateway_service.dto.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class FallbackController {

     @RequestMapping("/fallback/notification")
     public Mono<ResponseEntity<ApiResponse>> notificationFallback() {
          ResponseEntity<ApiResponse> response = ResponseEntity
                  .status(HttpStatus.SERVICE_UNAVAILABLE)
                  .body(ApiResponse.error(
                                  ResponseCode.SERVICE_UNAVAILABLE,
                                  "Ошибка в сервисе уведомлений")
                  );
          log.warn("Fallback for notification-service. Reason: service unavailable, ResponseCode: {}, Status: {}",
                  ResponseCode.SERVICE_UNAVAILABLE,
                  HttpStatus.SERVICE_UNAVAILABLE.value()
          );
          return Mono.just(response);
     }

     @RequestMapping("/fallback/user")
     public Mono<ResponseEntity<ApiResponse>> userFallback() {
          ResponseEntity<ApiResponse> response = ResponseEntity
                  .status(HttpStatus.SERVICE_UNAVAILABLE)
                  .body(ApiResponse.error(
                          ResponseCode.SERVICE_UNAVAILABLE,
                          "Ошибка в сервисе пользователей")
                  );
          log.warn("Fallback for user-service. Reason: service unavailable, ResponseCode: {}, Status: {}",
                  ResponseCode.SERVICE_UNAVAILABLE,
                  HttpStatus.SERVICE_UNAVAILABLE.value()
          );
          return Mono.just(response);
     }
}