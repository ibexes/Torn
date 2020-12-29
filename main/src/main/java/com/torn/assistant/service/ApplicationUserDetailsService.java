package com.torn.assistant.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.torn.api.client.VerifyApiClient;
import com.torn.api.model.exceptions.IncorrectKeyException;
import com.torn.api.model.faction.Member;
import com.torn.assistant.config.LoginCredentials;
import com.torn.assistant.persistence.dao.UserDao;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.security.core.userdetails.User.withUsername;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserService userService;

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
                Member member = VerifyApiClient.verify(user.getApiKey());
                if (Long.valueOf(8151).equals(member.getFactionId())) {
                    builder = withUsername(username);
                    builder.password(user.getApiKey());
                    builder.authorities("ROLE_USER");
                } else {
                    throw new UsernameNotFoundException("user is not in London");
                }
            } catch (JsonProcessingException | IncorrectKeyException e) {
                throw new UsernameNotFoundException("unable to login using api key");
            }
        }

        return builder.build();
    }

    public Long getUserId(String username) {
        Pattern p = Pattern.compile("\\w+\\[(\\d+)]");
        Matcher m = p.matcher(username);
        if(m.find()) {
            return Long.valueOf(m.group(1));
        }
        return null;
    }
}
