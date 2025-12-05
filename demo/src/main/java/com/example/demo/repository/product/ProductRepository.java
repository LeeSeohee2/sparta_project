package com.example.demo.repository.product;

import com.example.demo.domain.product.Product;        // Product 엔티티 import
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository
        extends JpaRepository<Product, Long> {         // <엔티티, PK 타입>
    // 기본 CRUD 메서드(findById, save 등)는 JpaRepository 가 자동으로 제공함
}