package com.torn.assistant.config;

import com.torn.assistant.service.AuthenticationService;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(AuthenticationService authenticationService, UserDetailsService applicationUserDetailsService) {
        this.authenticationService = authenticationService;
        this.userDetailsService = applicationUserDetailsService;
    }


    @Override
    protected void configure(final AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationService);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/h2-console/**").hasRole("ADMIN")
                .antMatchers("/api/**").hasRole("USER")
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()

                // remember the session, change this to persistent token in future
                .rememberMe()
                .key("K4vuvJMxmQYyNjsVtKMdMeUyHhTmcw3vb8QfC")
                .userDetailsService(userDetailsService)
                .alwaysRemember(true)
                .tokenValiditySeconds(86400 * 31)
                .and()

                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)
                .permitAll();

    }
}
