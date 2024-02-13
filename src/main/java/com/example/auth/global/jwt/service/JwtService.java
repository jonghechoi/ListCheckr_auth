package com.example.auth.global.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Optional;

public interface JwtService {
    public String createAccessToken(String uid);

    public String createRefreshToken();

    public void sendAccessToken(HttpServletResponse response, String accessToken);

    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken);

    public Optional<String> extractAccessToken(HttpServletRequest request);

    public Optional<String> extractRefreshToken(HttpServletRequest request);

    public Optional<String> extractUid(HttpServletRequest request);

    public Optional<String> extractUidByAccessToken(String accessToken);

    public void setAccessTokenHeader(HttpServletResponse response, String accessToken);

    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);

    public boolean updateRefreshToken(String uid, String refreshToken);

    public boolean isTokenValid(String token);
}
