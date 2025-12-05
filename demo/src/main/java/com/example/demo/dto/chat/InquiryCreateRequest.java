package com.example.demo.dto.chat;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter                                           // getter 자동 생성
@NoArgsConstructor                                // 기본 생성자 생성
public class InquiryCreateRequest {

    private Long productId;                       // 어떤 상품에 대한 문의인지
    private String content;                       // 첫 문의 내용(첫 메시지로 저장할 텍스트)
}