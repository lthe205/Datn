package com.example.datn.auth;

import com.example.datn.user.NguoiDung;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

	private final NguoiDung user;

	public CustomUserDetails(NguoiDung user) {
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		String roleName = user.getVaiTro() != null && user.getVaiTro().getTenVaiTro() != null
			? user.getVaiTro().getTenVaiTro()
			: "USER";
		return List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
	}

	@Override
	public String getPassword() {
		return user.getMatKhau();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() { return true; }
	@Override
	public boolean isAccountNonLocked() { return !user.isBiKhoa(); }
	@Override
	public boolean isCredentialsNonExpired() { return true; }
	@Override
	public boolean isEnabled() { return user.isHoatDong(); }

	public NguoiDung getDomainUser() { return user; }
} 