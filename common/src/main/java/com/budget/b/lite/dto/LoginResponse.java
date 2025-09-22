package com.budget.b.lite.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
@Getter
@Setter
public class LoginResponse {
    private String token ;
    private String userName;
    private Collection<? extends GrantedAuthority> roles;
}
