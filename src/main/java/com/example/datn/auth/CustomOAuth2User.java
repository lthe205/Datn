package com.example.datn.auth;

import com.example.datn.user.NguoiDung;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User, UserDetails {
    private Map<String, Object> attributes;
    private NguoiDung user;

    public CustomOAuth2User(Map<String, Object> attributes, NguoiDung user) {
        this.attributes = attributes;
        this.user = user;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user.getVaiTro() != null) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getVaiTro().getTenVaiTro()));
        }
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    public NguoiDung getUser() {
        return user;
    }

    public String getEmail() {
        return user.getEmail();
    }

    public String getFullName() {
        return user.getTen();
    }

    public Long getUserId() {
        return user.getId();
    }

    // UserDetails methods
    @Override
    public String getPassword() {
        return user.getMatKhau() != null ? user.getMatKhau() : "";
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isBiKhoa();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isHoatDong();
    }
}
