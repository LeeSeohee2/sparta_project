package com.example.demo.domain.product;

import com.example.demo.domain.user.Users;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")   // MySQL 테이블명 products 로 강제 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                 // 상품 PK

    @Column(nullable = false)
    private String name;             // 상품명

    @Column(nullable = false)
    private Integer price;           // 상품 가격

    @Column(nullable = false)
    private String description;      // 상품 설명

    // 상품을 등록한 판매자 (users 테이블 FK)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Users seller;

    // 상품에 대한 간단한 setter (필요 시 사용)
    public void updateProduct(String name, Integer price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
}
