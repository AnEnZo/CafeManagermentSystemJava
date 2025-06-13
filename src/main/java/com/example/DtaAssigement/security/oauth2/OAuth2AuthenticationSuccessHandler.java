package com.example.DtaAssigement.security.oauth2;

import com.example.DtaAssigement.security.CustomUserDetails;
import com.example.DtaAssigement.security.JwtTokenUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

@Component
@AllArgsConstructor
public class OAuth2AuthenticationSuccessHandler
        implements AuthenticationSuccessHandler {

    private final JwtTokenUtil jwtTokenUtil;
    private final HttpCookieOAuth2AuthorizationRequestRepository authRequestRepo; // để clear cookie/state
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String token = jwtTokenUtil.generateToken(principal);

        // Lấy URL redirect từ cookie (nếu client đã truyền trước đó)
        String redirectUri = getRedirectUriFromCookie(request);

        // Nếu không có cookie, bạn có thể đặt luôn URL mặc định của SPA
        if (redirectUri == null || redirectUri.isBlank()) {
            redirectUri = "http://localhost:8080/oauth2-redirect.html";
        }

        // Xây dựng URL cuối cùng
        String targetUrl = UriComponentsBuilder
                .fromUriString(redirectUri)
                .queryParam("token", token)
                .build()
                .toUriString();

        // Xóa cookie state (nếu có)
        authRequestRepo.removeAuthorizationRequestCookies(request, response);

        // 2. Dùng redirectStrategy thay vì getRedirectStrategy()
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    private String getRedirectUriFromCookie(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> "redirect_uri".equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse("http://localhost:3000");  // URL mặc định của SPA
    }


}

