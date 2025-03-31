package com.traders.auth.service.dto;

public record AddEnquiryRequest(
        String name,
        String contactNo,
        String message
) {
}
