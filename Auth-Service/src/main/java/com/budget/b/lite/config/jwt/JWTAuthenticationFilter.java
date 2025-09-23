package com.budget.b.lite.config.jwt;

import com.budget.b.lite.services.CustomUserDetailsService;
import com.budget.b.lite.services.RefreshTokenService;
import com.budget.b.lite.utils.exception.custom_exceptions.InvalidTokenException;
import com.budget.b.lite.utils.jwt.JWTUtils;
import com.budget.b.lite.utils.user_config.UserInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final CustomUserDetailsService userDetailsService;
    private final JWTUtils jwtUtils;
    private final AuthenticationEntryPoint entryPoint;
    private final RefreshTokenService service;

    public JWTAuthenticationFilter(CustomUserDetailsService userDetailsService,
                                   JWTUtils jwtUtils,
                                   AuthenticationEntryPoint entryPoint,
                                   RefreshTokenService service) {
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.entryPoint = entryPoint;
        this.service = service;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

        return path.startsWith("/api/auth");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        try {


            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtUtils.extractEmailFromPayload(token);

                if (email != null) {
                    UserInfo userDetails = (UserInfo) userDetailsService.loadUserByUsername(email);

                    if (jwtUtils.isTokenValid(token, email, userDetails.getJwtSecret())) {
                        String path = request.getServletPath();

                        if("/api/acc/token/refresh".equals(path) && !checkRefresh(token))
                            throw new InvalidTokenException("That's not a refresh token");


                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } else {
                throw new InvalidTokenException("This Endpoint requires token");
            }
        } catch (Exception e) {
            entryPoint.commence(request, response, new AuthenticationServiceException(e.getMessage(), e));
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private boolean checkRefresh(String token){
        return service.findByToken(token).isPresent();
    }
}
