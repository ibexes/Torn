package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.client.VerifyApiClient;
import com.torn.api.model.exceptions.IncorrectKeyException;
import com.torn.api.model.faction.Member;
import com.torn.assistant.config.LoginCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AuthenticationService implements AuthenticationProvider {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getCredentials().toString().isEmpty()) {
            // do api key verification
            String apiKey = authentication.getName();
            try {
                Member member = VerifyApiClient.verify(apiKey);
                if (Long.valueOf(8151).equals(member.getFactionId())) {
                    logger.info("{} [{}] logged in using API key", member.getName(), member.getUserId());
                    return new UsernamePasswordAuthenticationToken(member.getUserId(), apiKey,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
                } else {
                    logger.warn("{} [{}] logged in using API key but is not in London", member.getName(), member.getUserId());
                    throw new BadCredentialsException("user is not in London");
                }
            } catch (JsonProcessingException | IncorrectKeyException e) {
                throw new BadCredentialsException("unable to login using api key");
            }
        } else {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            List<GrantedAuthority> authorityList = LoginCredentials.authenticate(username, password);
            if (authorityList != null) {
                logger.info("{} logged in using credentials", username);
                return new UsernamePasswordAuthenticationToken(username, password, authorityList);
            } else {
                throw new BadCredentialsException("1000");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
