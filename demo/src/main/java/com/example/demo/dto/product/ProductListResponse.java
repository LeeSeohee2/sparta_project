package com.example.demo.dto.product;


import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListResponse {

    private Long id;          // 상품 ID
    private String name;      // 상품명
    private int price;        // 가격
}
