package com.example.auth.global.jwt.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.auth.domain.dto.UserRequestDtoFromKfaka;
import com.example.auth.global.jwt.service.JwtService;
import com.example.auth.repository.AuthRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    @Value("${jwt.uid.header}")
    private String uidHeader;

    /** JWT 표준 스펙 지정 */
    // https://javadoc.io/static/com.auth0/java-jwt/4.4.0/com/auth0/jwt/JWTCreator.Builder.html
    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String UID_CLAIM = "sub";
    private static final String BEARER = "Bearer ";

    private final AuthRepository authRepository;

    /** AccessToken 생성 */
    @Override
    public String createAccessToken(String uid) {
        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .withClaim(UID_CLAIM, uid)
                .sign(Algorithm.HMAC512(secretKey));
    }

    /** RefreshToken 생성 */
    @Override
    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    /** AccessToken 헤더 전달 */
    @Override
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    /** AccessToken & RefreshToken 헤더 전달 */
    @Override
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
    }

    /** AccessToken 추출 */
    @Override
    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(accessToken -> accessToken.startsWith(BEARER))
                .map(accessToken -> accessToken.replace(BEARER, ""));
    }

    /** RefreshToken 추출 */
    @Override
    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    /** Uid 추출 */
    @Override
    public Optional<String> extractUid(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(uidHeader))
                .filter(uid -> uid.startsWith(BEARER))
                .map(uid -> uid.replace(BEARER, ""));
    }

    /** Uid 추출 (with accessToken) */
    @Override
    public Optional<String> extractUidByAccessToken(String accessToken) {
        try {
            return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey)) // Verification builder return
                    .build()
                    .verify(accessToken)
                    .getClaim(UID_CLAIM)
                    .asString());
        }
        catch(Exception e) {
            log.info("액세스 토큰 이 유효하지 않습니다 : {}", accessToken);
            return Optional.empty();
        }
    }

    /** AccessToken 헤더 설정 */
    @Override
    public void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    /** RefreshToken 헤더 설정 */
    @Override
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    /** RefreshToken DB 저장 및 업데이트 */
    @Override
    public boolean updateRefreshToken(String uid, String refreshToken) {
        if(authRepository.isUidExist(uid)) {
            UserRequestDtoFromKfaka userRequestDtoFromKfaka = authRepository.getUser(uid);
            userRequestDtoFromKfaka.setRefreshToken(refreshToken);

            if(authRepository.updateUser(userRequestDtoFromKfaka)) {
                log.info("RefreshToken 업데이트 완료");
                return true;
            }
            else {
                log.info("RefreshToken 업데이트 실패");
            }
        }
        return false;
    }

    /** Token 검증 */
    @Override
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        }
        catch(Exception e) {
            return false;
        }
    }
}
