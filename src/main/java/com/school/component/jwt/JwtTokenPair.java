package com.school.component.jwt;

import java.io.Serializable;

public class JwtTokenPair implements Serializable {
    private String accessToken;
    private String refreshToken;

    public JwtTokenPair() {
    }

    public JwtTokenPair(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
