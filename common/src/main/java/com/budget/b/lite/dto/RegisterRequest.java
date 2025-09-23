package com.budget.b.lite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(String username,
                              @NotBlank(message = "Email is required")
                              @Email(message = "Invalid email format")
                              String email,
                              @NotBlank(message = "Password is required")
                              @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
                              @Pattern(
                                      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$",
                                      message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character"
                              )
                              String password) {
}
