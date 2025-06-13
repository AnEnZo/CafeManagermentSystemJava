package com.example.DtaAssigement.service.impl;

import com.example.DtaAssigement.entity.User;
import com.example.DtaAssigement.repository.UserRepository;
import com.example.DtaAssigement.security.CustomUserDetails;
import com.example.DtaAssigement.service.UserService;
import lombok.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.*;

import java.util.Map;

@Service
@AllArgsConstructor
public class CustomOAuth2UserService
        extends DefaultOAuth2UserService {

    private final UserService userService; // service xử lý logic register/update

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest
                .getClientRegistration().getRegistrationId(); // google, facebook…
        String userNameAttr = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName(); // "sub" cho Google

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = attributes.get(userNameAttr).toString();
        String email = attributes.get("email").toString();
        String displayname = attributes.get("name").toString();
        String username = registrationId + "_" + providerId;    // ví dụ: "google_1074628347293847293"
        // Xử lý lưu hoặc cập nhật user
        User user = userService.processOAuthUser(registrationId, providerId, email, username, displayname);

        // Build Spring Security user principal
        return CustomUserDetails.fromOAuth2User(user, oAuth2User.getAttributes());
    }
}
