package com.example.demo.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
// 요청값 검증을 위한 어노테이션(@Email, @NotBlank)

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// getter 자동 생성

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @Email                       // 이메일 형식 검증
    private String email;

    @NotBlank                    // 공백/빈 값 불가
    private String password;

    @NotBlank
    private String name;

    @NotBlank                    // buyer 또는 seller 입력
    private String role;
}