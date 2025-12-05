package com.example.demo.dto.product;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponse {

    private Long id;            // 상품 ID
    private String name;        // 상품명
    private int price;          // 상품 가격
    private String description; // 상품 설명
    private String sellerName;  // 판매자 이름
}
