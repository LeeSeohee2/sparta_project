package com.example.demo.domain.user;

import jakarta.persistence.*;
// JPA 엔티티 관련 어노테이션들 (@Entity, @Id 등)

import lombok.*;
// Lombok: Getter, Builder, 생성자 자동 생성에 필요

import java.time.LocalDateTime;
// created_at 날짜 저장에 사용

@Entity                      // 이 클래스가 JPA 엔티티(테이블 매핑 대상)임을 의미
@Table(name = "users")       // 실제 DB 테이블명을 users로 지정
@Getter                      // Lombok: 모든 필드에 대해 getter 메서드 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// JPA가 기본 생성자를 필요로 하기 때문에 protected 생성자 자동 생성
public class Users {

    @Id                      // 기본키(PK)임을 선언
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // MySQL AUTO_INCREMENT 방식으로 PK 생성
    @Column(name = "user_id")
    private Long userId;    // 사용자 고유 ID (users.user_id 컬럼 매핑)

    @Column(nullable = false, unique = true)
    // null 불가 + 중복 불가 (이메일은 유일해야 함)
    private String email;

    @Column(nullable = false)
    // 반드시 값이 있어야 하는 컬럼
    private String password_hash;
    // 암호화된 비밀번호 저장

    @Column(nullable = false)
    private String name;     // 사용자 이름

    @Column(nullable = false)
    private String role;     // buyer / seller / admin 역할 구분

    @Column(nullable = false)
    private LocalDateTime created_at;
    // 가입 날짜 기록

    @Builder                 // 빌더 패턴 사용 가능하게 함
    public Users(String email, String password_hash, String name, String role) {
        this.email = email;
        this.password_hash = password_hash;
        this.name = name;
        this.role = role;
        this.created_at = LocalDateTime.now(); // 생성 시각 자동 기록
    }
}
