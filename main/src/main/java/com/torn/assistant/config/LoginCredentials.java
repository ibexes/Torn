package com.torn.assistant.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public enum LoginCredentials {
    JIA("jia", "mrpops3ko123", "ROLE_ADMIN", "ROLE_USER"),
    USER("user", "S[Jgw3VP4z6L£",  "ROLE_USER"),
    POPS("pops", "mrpops3ko123", "ROLE_ADMIN", "ROLE_USER");

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    LoginCredentials(String username, String password, String... roles) {
        this.username = username;
        this.password = password;
        this.authorities = new ArrayList<>();

        for(String role : roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
    }

    public static List<GrantedAuthority> authenticate(String username, String password) {
        for(LoginCredentials loginCredentials : LoginCredentials.values()) {
            if(loginCredentials.username.equals(username) && loginCredentials.password.equals(password)) {
                return loginCredentials.authorities;
            }
        }
        return null;
    }
}
