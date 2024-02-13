package com.example.auth.global.jwt.filter;

import com.example.auth.domain.User;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.global.jwt.service.impl.JwtServiceImpl;
import com.example.auth.login.service.impl.UserDetailsImpl;
import com.example.auth.repository.AuthRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/user/login";
    private final JwtServiceImpl jwtService;
    private final AuthRepository authRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        if(refreshToken != null) {
            jwtService.extractUid(request) // UID Claim 추출
                    .ifPresent(uid -> {
                        UserRequestDtoFromKfaka userRequestDtoFromKfaka = authRepository.getUser(uid); // Redis에서 UID로 유저 정보 추출
                        checkRefreshTokenAndReIssueAccessToken(response, refreshToken, userRequestDtoFromKfaka); // AccessToken & RefreshToken 재발행
                    });
        }
        else {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }

        filterChain.doFilter(request, response);
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshTokenFromReq, UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        if(refreshTokenFromReq.equals(userRequestDtoFromKfaka.getRefreshToken())) {
            String reIssuedRefreshToken = reIssueRefreshToken(userRequestDtoFromKfaka);
            jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(userRequestDtoFromKfaka.getUid()), reIssuedRefreshToken);
        }
    }

    private String reIssueRefreshToken(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        userRequestDtoFromKfaka.setRefreshToken(reIssuedRefreshToken);

        if(authRepository.updateUser(userRequestDtoFromKfaka)) {
            return reIssuedRefreshToken;
        }
        else {
            throw new RuntimeException("RefreshToken 생성 및 저장 실패");
        }
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> jwtService.extractUidByAccessToken(accessToken)
                        .ifPresent(uid -> {
                            if(authRepository.isUidExist(uid)) {
                                UserRequestDtoFromKfaka userRequestDtoFromKfaka = authRepository.getUser(uid);
                                saveAuthentication(userRequestDtoFromKfaka);
                            }
                        }));
        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        User user = new User(userRequestDtoFromKfaka.getUid(),
                        userRequestDtoFromKfaka.getPassword(),
                        userRequestDtoFromKfaka.getName(),
                        userRequestDtoFromKfaka.getRole());

        UserDetails userDetails = new UserDetailsImpl(user, userRequestDtoFromKfaka.getUid());
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
