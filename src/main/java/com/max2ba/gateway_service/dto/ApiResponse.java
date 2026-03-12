package com.max2ba.gateway_service.dto;

public record ApiResponse(
        ResponseCode code,
        String message,
        String details
) {
     public static ApiResponse error(ResponseCode code, String details) {
          return new ApiResponse(code, code.message(), details);
     }
}
