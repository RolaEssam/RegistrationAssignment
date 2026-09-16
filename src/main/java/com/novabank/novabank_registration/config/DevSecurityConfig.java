package com.novabank.novabank_registration.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain devSecurityFilterChain(
            HttpSecurity http) throws Exception {

        http
                .csrf(csrf ->
                        csrf
                                .ignoringRequestMatchers(
                                        "/h2-console/**"
                                )
                                .disable()
                )

                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers(
                                        "/api/v1/auth/register",
                                        "/h2-console/**"
                                )
                                .permitAll()

                                .anyRequest()
                                .authenticated()
                )

                .headers(headers ->
                        headers
                                .frameOptions(frame ->
                                        frame.sameOrigin()
                                )
                )

                .formLogin(form ->
                        form.disable()
                )

                .httpBasic(basic ->
                        basic.disable()
                );

        return http.build();
    }
}