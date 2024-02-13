package com.example.auth.global.config;

import com.example.auth.global.jwt.filter.JwtAuthenticationProcessingFilter;
import com.example.auth.global.jwt.service.impl.JwtServiceImpl;
import com.example.auth.login.filter.LoginAuthenticationProcessingFilter;
import com.example.auth.login.service.impl.UserDetailsServiceImpl;
import com.example.auth.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtServiceImpl jwtService;
    private final AuthRepository authRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
//                                .requestMatchers("/user/login").permitAll()
//                                .requestMatchers("/**").hasRole("USER")
                                .requestMatchers("/**").permitAll()
                )
                .addFilterBefore(LoginAuthenticationProcessingFilter.builder()
                        .userDetailsService(userDetailsService)
                        .passwordEncoder(encodePassword())
                        .build(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationProcessingFilter(jwtService, authRepository), LoginAuthenticationProcessingFilter.class)
                .build();
    }

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }
}
