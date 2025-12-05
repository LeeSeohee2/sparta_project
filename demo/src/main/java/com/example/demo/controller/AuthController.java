package com.example.demo.controller;


import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.SignUpRequest;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;            // DTO 검증 활성화

import java.util.Map;

@RestController                           // REST API 컨트롤러 역할
@RequestMapping("/api/auth")              // 로그인/회원가입 URL 그룹
@RequiredArgsConstructor                  // 의존성 자동 주입
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")               // POST /api/auth/signup
    public ResponseEntity<?> signup(
            @RequestBody @Valid SignUpRequest request) {
        // 요청 body → DTO 변환 + 검증

        authService.signup(request);      // 회원가입 수행

        return ResponseEntity.ok(
                Map.of("message", "회원가입 완료"));
        // JSON 응답
    }

    @PostMapping("/login")                // POST /api/auth/login
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse token = authService.login(request);
        // 로그인 수행

        return ResponseEntity.ok(token);   // JWT 응답
    }
}
