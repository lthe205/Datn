package com.example.datn.config;

import com.example.datn.auth.GoogleOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

	@Autowired
	private GoogleOAuth2UserService googleOAuth2UserService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return org.springframework.security.crypto.password.NoOpPasswordEncoder.getInstance();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
				.requestMatchers("/login", "/dang-nhap", "/error", "/register", "/dang-ky", "/verify").permitAll()
				.requestMatchers("/", "/tim-kiem", "/san-pham/**", "/danh-muc/**", "/thuong-hieu/**").permitAll()
				.requestMatchers("/admin/**").hasRole("Admin")
				.requestMatchers("/thong-tin-ca-nhan/**").authenticated()
				.requestMatchers("/don-hang-cua-toi/**").authenticated()
				.requestMatchers("/yeu-thich/**").authenticated()
				.anyRequest().permitAll()
			)
			.formLogin(form -> form
				.loginPage("/dang-nhap")
				.loginProcessingUrl("/login")
				.usernameParameter("username")
				.passwordParameter("password")
				.defaultSuccessUrl("/", true)
				.failureUrl("/dang-nhap?error=true")
				.permitAll()
			)
			.oauth2Login(oauth2 -> oauth2
				.loginPage("/dang-nhap")
				.defaultSuccessUrl("/", true)
				.failureUrl("/dang-nhap?error=true")
				.userInfoEndpoint(userInfo -> userInfo
					.oidcUserService(googleOAuth2UserService)
				)
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/dang-nhap?logout")
				.permitAll()
			);
		return http.build();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return provider;
	}
} 