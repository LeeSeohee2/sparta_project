package com.example.demo.security.jwt;

import io.jsonwebtoken.Claims;
// JWT payload 정보(Claims)를 다룰 때 필요

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// 서블릿 필터 동작 시 필요한 요청/응답 객체

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// 인증 객체 생성

import org.springframework.security.core.authority.SimpleGrantedAuthority;
// ROLE_xxx 권한 설정용

import org.springframework.security.core.context.SecurityContextHolder;
// 인증 정보를 스프링 시큐리티 컨텍스트에 저장하는 곳

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;
// 요청당 한 번만 실행되는 필터

import java.io.IOException;
import java.util.List;

@Component                                                       // 스프링 빈으로 등록
@RequiredArgsConstructor                                         // 생성자 자동 주입
public class JwtFilter extends OncePerRequestFilter {            // 요청당 한 번 실행되는 필터

    private final JwtTokenProvider jwtTokenProvider;             // 우리가 만든 JWT 생성/검증 클래스

    @Override
    protected void doFilterInternal(HttpServletRequest request,  // 실제 필터 동작 메서드
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);                   // Authorization 헤더에서 JWT 추출

        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 존재하고 정상인지 검사
            Claims claims = jwtTokenProvider.getClaims(token);     // payload(Claims) 추출

            String email = claims.getSubject();                    // token subject = email
            String role = claims.get("role", String.class);        // payload에서 role 꺼내기

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,                                  // 인증 principal(사용자 이름)
                            null,                                   // 사용자 credentials(비밀번호) — 필요 없음
                            List.of(new SimpleGrantedAuthority("ROLE_" + role))
                            // 권한: ROLE_buyer / ROLE_seller / ROLE_admin
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 현재 요청을 인증된 사용자로 등록
        }

        filterChain.doFilter(request, response);                    // 다음 필터로 요청 전달
    }

    private String resolveToken(HttpServletRequest request) {       // Authorization 헤더에서 JWT 추출
        String bearerToken = request.getHeader("Authorization");    // Authorization 헤더 확인

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // "Bearer <token>" 형식인지 확인
            return bearerToken.substring(7);                        // "Bearer " 이후의 텍스트 = token
        }
        return null;                                                // 없으면 null 반환
    }
}