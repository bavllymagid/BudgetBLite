package com.budget.b.lite.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class TokenUtils {

    /**
     * Extracts email from the request attribute set by UserAuthenticationFilter
     * @return email from request attribute, or null if not found
     */
    public String extractEmailFromToken() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            return null;
        }

        return (String) request.getAttribute("email");
    }

    private HttpServletRequest getCurrentHttpRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }
}
