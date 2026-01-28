package com.binbang.backend.member.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인/토큰 재발급 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;  // Access Token 만료 시간 (초 단위)

    /**
     * 기본 응답 생성 (tokenType = "Bearer")
     *
     * 그니까 access token, refresh token, 만료기한 이거 3개만 정해주면
     * 나머지 값을들 채워서 AuthResponse를 만들어줌
     */
    public static AuthResponse of(String accessToken, String refreshToken, Long expiresIn) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(expiresIn)
                .build();
    }
}