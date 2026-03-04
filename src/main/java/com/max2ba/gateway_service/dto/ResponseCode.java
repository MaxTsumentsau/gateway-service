package com.max2ba.gateway_service.dto;

public enum ResponseCode {
     INTERNAL_ERROR("Ошибка Gateway-service"),
     ROUTE_NOT_FOUND("Маршрут не найден"),
     SERVICE_UNAVAILABLE("Целевой сервис недоступен"),
     TIMEOUT("Превышено время ожидания ответа от сервиса"),
     DNS_ERROR("Ошибка DNS при обращении к сервису"),
     CIRCUIT_BREAKER_OPEN("Circuit breaker открыт"),
     UNEXPECTED_ERROR("Непредвиденная ошибка");

     private final String defaultMessage;

     ResponseCode(String defaultMessage) {
          this.defaultMessage = defaultMessage;
     }

     public String message() {
          return defaultMessage;
     }
}
