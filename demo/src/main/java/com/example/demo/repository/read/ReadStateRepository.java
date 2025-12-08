package com.example.demo.repository.read;

import com.example.demo.domain.read.ReadState;                 // ReadState 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository                                                    // 레포지토리 빈 등록
public interface ReadStateRepository
        extends JpaRepository<ReadState, Long> {               // <엔티티, PK 타입>

    // ⭐ 추가된 부분: roomId로 모든 ReadState 조회
    List<ReadState> findByRoomId(Long roomId);


    // 특정 사용자(userId)가 특정 방(roomId)에 대해 가지고 있는 읽음 상태 한 건 조회
    Optional<ReadState> findByRoomIdAndUserId(Long roomId, Long userId);
}