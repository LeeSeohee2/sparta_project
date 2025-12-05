package com.example.demo.security.jwt;


import io.jsonwebtoken.*;                       // JWT 생성/파싱 라이브러리
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component                                       // 스프링 빈 등록
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")                      // application.yml 에서 가져옴
    private String secretKey;

    private final long accessTokenValidTime = 60 * 60 * 1000;
    // 토큰 유효기간: 1시간

    public String createToken(Long userId, String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        // token subject(email)
        claims.put("userId", userId);            // payload userId
        claims.put("role", role);                // payload role

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)               // payload 설정
                .setIssuedAt(now)                // 발급 시간
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey)
                // 서명 알고리즘 + 비밀키
                .compact();                      // JWT 문자열 생성
    }

    public boolean validateToken(String token) { // 토큰 유효성 검사
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token) {      // payload 추출
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}