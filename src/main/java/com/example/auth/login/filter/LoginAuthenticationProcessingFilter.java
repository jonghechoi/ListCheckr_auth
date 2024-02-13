package com.example.auth.login.filter;

import com.example.auth.global.jwt.service.impl.JwtServiceImpl;
import com.example.auth.login.service.impl.UserDetailsImpl;
import com.example.auth.login.service.impl.UserDetailsServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

@Slf4j
@Builder
public class LoginAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String ROLE = "ROLE";

    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtServiceImpl jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals("/user/login")) {
            Map<String, String> requestBodyMap = getBody(request);
            String uid = requestBodyMap.get("uid");
            String password = requestBodyMap.get("password");

            if(uid != null && password != null) {
                // 유저 확인
                UserDetails userDetails = userDetailsService.loadUserByUsername(uid);

                // 비밀번호 확인
                if(!passwordEncoder.matches(password, userDetails.getPassword())) {
                    throw new IllegalAccessError("비밀번호가 일치하지 않습니다.");
                }

                // 헤더에 Role 등록
                UserDetailsImpl userDetailsImpl = (UserDetailsImpl) userDetails;
                response.setHeader(ROLE, userDetailsImpl.getUser().getRole().getAuthority());

                // 인증 객체 생성 및 등록
                SecurityContext context = SecurityContextHolder.getContext();
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                context.setAuthentication(authentication);

                SecurityContextHolder.setContext(context);

                // AccessToken, RefreshToken 생성 및 헤더 추가
                String accessToken = jwtService.createAccessToken(uid);
                String refreshToken = jwtService.createRefreshToken();

                jwtService.updateRefreshToken(uid, refreshToken);
                jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
            }
            else {
                throw new IllegalAccessError("입력한 아이디가 없거나 패스워드가 없습니다.");
            }
        }
        filterChain.doFilter(request, response);
    }

    private Map<String, String> getBody(HttpServletRequest request) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }

        String requestBody = stringBuilder.toString();

        // JSON 형식의 requestBody를 Map으로 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> requestBodyMap = objectMapper.readValue(requestBody, new TypeReference<Map<String, String>>() {});

        return requestBodyMap;
    }
}