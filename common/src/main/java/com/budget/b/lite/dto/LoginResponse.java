package com.budget.b.lite.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record LoginResponse(
    String token,
    @JsonProperty("refresh_token")
    String refreshToken,
    String email
)
{}
