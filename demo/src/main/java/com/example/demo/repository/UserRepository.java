package com.example.demo.repository;

import com.example.demo.domain.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
// 스프링 데이터 JPA 기본 인터페이스

import java.util.Optional;
// findByEmail 반환 타입

public interface UserRepository extends JpaRepository<Users, Long> {
// JpaRepository<엔티티타입, PK타입> → 기본 CRUD 자동 제공

    Optional<Users> findByEmail(String email);
    // 로그인용: 이메일로 사용자 조회

    boolean existsByEmail(String email);
    // 회원가입용: 이메일 중복 여부 체크
}