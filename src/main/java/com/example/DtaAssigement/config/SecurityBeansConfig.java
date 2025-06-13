package com.example.DtaAssigement.config;


import com.example.DtaAssigement.security.JwtTokenUtil;
import com.example.DtaAssigement.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.example.DtaAssigement.security.oauth2.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityBeansConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AuthenticationSuccessHandler oauth2SuccessHandler(
            JwtTokenUtil jwtTokenUtil,
            HttpCookieOAuth2AuthorizationRequestRepository repo
    ) {
        return new OAuth2AuthenticationSuccessHandler(jwtTokenUtil, repo);
    }


}

