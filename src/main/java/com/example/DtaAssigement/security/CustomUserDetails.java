package com.example.DtaAssigement.security;

import com.example.DtaAssigement.entity.Roles;
import com.example.DtaAssigement.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private Map<String,Object> attributes;

    public CustomUserDetails(User user) {
        this.user = user;
        this.attributes = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(Roles::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }


    // Factory cho username/password
    public static CustomUserDetails fromUser(User user) {
        return new CustomUserDetails(user);
    }

    // Factory cho OAuth2, có thêm attributes
    public static CustomUserDetails fromOAuth2User(User user, Map<String,Object> attrs) {
        CustomUserDetails principal = new CustomUserDetails(user);
        principal.attributes = attrs;
        return principal;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        // Trả providerId, hoặc user.getId().toString()
        return attributes != null
                ? attributes.get("sub").toString()
                : user.getId().toString();
    }


    public String getDisplayName() {
        return user.getDisplayName();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return user.getId();
    }
}

