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

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/user/login";
    private static final String NO_CHECK_URL_TEST = "/user/login/save-test";
    private final JwtServiceImpl jwtService;
    private final AuthRepository authRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response);
            return;
        }

        if(request.getRequestURI().equals(NO_CHECK_URL_TEST)) {
            log.error("JwtAuthenticationProcessingFilter 필터 들어옴");
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
                        checkRefreshTokenAndReIssueAccessToken(response, refreshToken, userRequestDtoFromKfaka); // AccessToken 재발행
                    });
            filterChain.doFilter(request, response);
            return;
        }

        if(refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshTokenFromReq, UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        if(refreshTokenFromReq.equals(userRequestDtoFromKfaka.getRefreshToken())) {
            String reIssuedRefreshToken = reIssueRefreshToken(userRequestDtoFromKfaka);
            jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(userRequestDtoFromKfaka.getUid()), reIssuedRefreshToken);
        }
    }

    private String reIssueRefreshToken(UserRequestDtoFromKfaka userRequestDtoFromKfaka) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();;
        boolean checkUpdateRefreshToken = userRequestDtoFromKfaka.updateRefreshToken(reIssuedRefreshToken);

        if(checkUpdateRefreshToken) {
            return reIssuedRefreshToken;
        }
        else {
            throw new RuntimeException("리프레시 토큰 생성 & 저장 실패~~");
        }
    }

    // AccessToken 체크 & 인증 처리
    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");

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
