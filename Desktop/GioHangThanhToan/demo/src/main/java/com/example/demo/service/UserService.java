package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.entity.VaiTro;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VaiTroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findActiveUserByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return user.get();
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public User save(User user) {
        // Encode password nếu chưa được encode
        if (user.getMatKhau() != null && !user.getMatKhau().startsWith("$2a$")) {
            user.setMatKhau(passwordEncoder.encode(user.getMatKhau()));
        }
        return userRepository.save(user);
    }
    
    public VaiTro getDefaultRole() {
        return vaiTroRepository.findByTenVaiTro("USER")
                .orElseGet(() -> {
                    // Tạo role USER nếu chưa có
                    VaiTro userRole = new VaiTro();
                    userRole.setTenVaiTro("USER");
                    userRole.setMoTa("Người dùng thông thường");
                    return vaiTroRepository.save(userRole);
                });
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public Optional<User> findByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId);
    }
}
