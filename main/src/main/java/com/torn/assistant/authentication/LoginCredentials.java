package com.torn.assistant.authentication;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public enum LoginCredentials {
    JIA("jia", "mrpops3ko123", 8151L, "ROLE_ADMIN", "ROLE_USER"),
    USER("user", "S[Jgw3VP4z6L£",  8151L, "ROLE_USER"),
    TEST("test", "test",  1L, "ROLE_USER"),
    POPS("pops", "mrpops3ko123", 8151L, "ROLE_ADMIN", "ROLE_USER");

    private String username;
    private String password;
    private Long factionId;
    private List<GrantedAuthority> authorities;

    LoginCredentials(String username, String password, Long factionId, String... roles) {
        this.username = username;
        this.password = password;
        this.factionId = factionId;
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

    public static LoginCredentials getUser(String username) {
        for(LoginCredentials loginCredentials : LoginCredentials.values()) {
            if(loginCredentials.username.equals(username)) {
                return loginCredentials;
            }
        }
        return null;
    }

    public String getUsername() {
        return username;
    }

    public Long getFactionId() {
        return factionId;
    }

    public String getPassword() {
        return password;
    }

    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }
}
