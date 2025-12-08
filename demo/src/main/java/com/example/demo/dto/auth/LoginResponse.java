package com.example.demo.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor                // 생성자를 자동으로 만들어줌
@NoArgsConstructor
@Setter
public class LoginResponse {

    private String accessToken;    // JWT Access Token 반환
    private String tokenType = "Bearer";
    private Long userId;

    public LoginResponse(String accessToken,Long userId) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.userId = userId;
    }
}