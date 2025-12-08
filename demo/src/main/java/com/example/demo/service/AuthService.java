package com.example.demo.service;




import com.example.demo.domain.user.Users;
import com.example.demo.dto.auth.LoginRequest;
import com.example.demo.dto.auth.LoginResponse;
import com.example.demo.dto.auth.SignUpRequest;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service                                           // 서비스 레이어 클래스
@RequiredArgsConstructor                           // final 필드 생성자 자동 주입
public class AuthService {

    private final UserRepository userRepository;   // DB 접근
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 빈
    private final JwtTokenProvider jwtTokenProvider; // JWT 생성 및 검증

    public void signup(SignUpRequest req) {        // 회원가입 로직

        if (userRepository.existsByEmail(req.getEmail())) {
            // 이메일 중복 체크
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        if (req.getRole().equals("admin")) {        // admin 가입 차단
            throw new IllegalArgumentException("admin 계정은 생성할 수 없습니다.");
        }

        Users user = Users.builder()                // Users 엔티티 생성
                .email(req.getEmail())
                .password_hash(passwordEncoder.encode(req.getPassword()))
                // 비밀번호 암호화
                .name(req.getName())
                .role(req.getRole())
                .build();

        userRepository.save(user);                  // DB 저장
    }

    public LoginResponse login(LoginRequest req) {  // 로그인 로직

        Users user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 이메일 존재 여부 체크

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword_hash())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            // 비밀번호 일치 여부 확인
        }

        String token = jwtTokenProvider.createToken(
                user.getUserId(), user.getEmail(), user.getRole());
        // JWT 생성

        return new LoginResponse(token,user.getUserId());             // 로그인 성공 응답(JSON)
    }
}