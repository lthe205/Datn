package com.example.datn.auth;

import com.example.datn.user.NguoiDung;
import com.example.datn.user.NguoiDungRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthHelper {
    
    private static ApplicationContext applicationContext;
    
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        AuthHelper.applicationContext = applicationContext;
    }
    
    public static NguoiDung getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getName())) {
            return null;
        }
        
        // Xử lý cho Google OAuth2 user
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
            return oauth2User.getUser();
        } else if (authentication.getPrincipal() instanceof CustomUserDetails) {
            // Xử lý cho user thường
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getDomainUser();
        } else if (authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser) {
            // Xử lý cho OIDC user (Google OAuth2)
            org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser = 
                (org.springframework.security.oauth2.core.oidc.user.OidcUser) authentication.getPrincipal();
            
            System.out.println("Debug - OIDC User found: " + oidcUser.getEmail());
            
            // Tìm user trong database bằng email
            String email = oidcUser.getEmail();
            if (email != null && applicationContext != null) {
                NguoiDungRepository repository = applicationContext.getBean(NguoiDungRepository.class);
                NguoiDung user = repository.findByEmail(email).orElse(null);
                System.out.println("Debug - User found in DB: " + (user != null ? user.getTen() : "null"));
                if (user != null) {
                    System.out.println("Debug - User avatar URL: " + user.getAvatarUrl());
                    System.out.println("Debug - User provider: " + user.getProvider());
                }
                return user;
            }
        }
        
        return null;
    }
}
