package com.example.demo.controller.product;

import com.example.demo.dto.product.ProductDetailResponse;
import com.example.demo.dto.product.ProductListResponse;
import com.example.demo.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController                                 // REST 컨트롤러
@RequestMapping("/api/products")               // /api/products 로 공통 URL 설정
@RequiredArgsConstructor                        // 의존성 자동 주입
public class ProductController {

    private final ProductService productService;

    // 상품 목록 조회
    @GetMapping
    public ResponseEntity<List<ProductListResponse>> getProducts() {
        return ResponseEntity.ok(productService.getProducts());
    }

    // 상품 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }
}
