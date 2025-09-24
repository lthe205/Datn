package com.example.datn.auth;

import com.example.datn.user.NguoiDung;
import com.example.datn.user.NguoiDungRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

	private final NguoiDungRepository nguoiDungRepository;

	public CustomUserDetailsService(NguoiDungRepository nguoiDungRepository) {
		this.nguoiDungRepository = nguoiDungRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		NguoiDung user = nguoiDungRepository.findByEmail(username)
			.orElseThrow(() -> new UsernameNotFoundException("Email không tồn tại"));
		return new CustomUserDetails(user);
	}
} 