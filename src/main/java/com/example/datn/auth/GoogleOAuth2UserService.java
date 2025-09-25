package com.example.datn.auth;

import com.example.datn.user.NguoiDung;
import com.example.datn.user.NguoiDungRepository;
import com.example.datn.user.VaiTro;
import com.example.datn.user.VaiTroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class GoogleOAuth2UserService extends OidcUserService {

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        
        try {
            return processOidcUser(userRequest, oidcUser);
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(new OAuth2Error("oauth2_user_processing_error", "Error processing OIDC user", null), ex);
        }
    }

    private OidcUser processOidcUser(OidcUserRequest userRequest, OidcUser oidcUser) {
        Map<String, Object> attributes = oidcUser.getAttributes();
        
        String googleId = (String) attributes.get("sub");
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");
        
        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException(new OAuth2Error("oauth2_email_not_found", "Email not found from OAuth2 provider", null));
        }

        Optional<NguoiDung> existingUser = nguoiDungRepository.findByEmail(email);
        NguoiDung user;

        if (existingUser.isPresent()) {
            // User exists, update Google ID if not set
            user = existingUser.get();
            System.out.println("Debug - Existing user found: " + user.getTen() + " (" + user.getEmail() + ")");
            if (user.getGoogleId() == null) {
                user.setGoogleId(googleId);
                user.setProvider("google");
                user.setAvatarUrl(picture);
                user.setNgayCapNhat(LocalDateTime.now());
                nguoiDungRepository.save(user);
                System.out.println("Debug - Updated existing user with Google info");
            }
        } else {
            // Create new user
            user = new NguoiDung();
            user.setGoogleId(googleId);
            user.setProvider("google");
            user.setEmail(email);
            user.setTen(name);
            user.setAvatarUrl(picture);
            user.setHoatDong(true);
            user.setBiKhoa(false);
            user.setNgayTao(LocalDateTime.now());
            user.setNgayCapNhat(LocalDateTime.now());
            
            // Set a default password for Google OAuth users (they won't use it)
            user.setMatKhau("GOOGLE_OAUTH_USER");
            
            // Set default role (Khách hàng)
            VaiTro userRole = vaiTroRepository.findByTenVaiTro("Khách hàng")
                .orElseThrow(() -> new RuntimeException("User role not found"));
            user.setVaiTro(userRole);
            
            nguoiDungRepository.save(user);
            System.out.println("Debug - Created new user: " + user.getTen() + " (" + user.getEmail() + ")");
        }

        System.out.println("Debug - Returning CustomOAuth2User for: " + user.getTen());
        // Return the original OIDC user since CustomOAuth2User doesn't implement OidcUser
        return oidcUser;
    }
}
