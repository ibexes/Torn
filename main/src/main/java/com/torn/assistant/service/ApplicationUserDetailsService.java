package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.client.VerifyApiClient;
import com.torn.api.model.exceptions.TornApiAccessException;
import com.torn.api.model.faction.Member;
import com.torn.assistant.config.LoginCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.torn.assistant.utils.UserUtils.getUserId;
import static org.springframework.security.core.userdetails.User.withUsername;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(ApplicationUserDetailsService.class);

    public ApplicationUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User.UserBuilder builder;

        LoginCredentials loginCredentials = LoginCredentials.getUser(username);
        if (loginCredentials != null) {
            builder = withUsername(username);
            builder.password(loginCredentials.getPassword());
            builder.authorities(loginCredentials.getAuthorities());
        } else {
            // try API key
            try {
                com.torn.assistant.persistence.entity.User user = userService.findByUserId(getUserId(username)).orElseThrow(() -> new UsernameNotFoundException("User not found."));
                logger.info("Verifying api key for {}", username);
                Member member = VerifyApiClient.verify(user.getApiKey());
                user = userService.saveOrUpdate(member, user.getApiKey());

                builder = withUsername(username);
                builder.password(user.getApiKey());
                builder.authorities("ROLE_USER");
            } catch (JsonProcessingException | TornApiAccessException e) {
                throw new UsernameNotFoundException("unable to login using api key");
            }
        }

        return builder.build();
    }
}
