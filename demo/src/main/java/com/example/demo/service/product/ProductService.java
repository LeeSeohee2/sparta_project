package com.example.demo.service.product;

import com.example.demo.domain.product.Product;
import com.example.demo.dto.product.ProductDetailResponse;
import com.example.demo.dto.product.ProductListResponse;
import com.example.demo.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service                                     // 서비스 빈 등록
@RequiredArgsConstructor                     // 생성자 자동 주입
public class ProductService {

    private final ProductRepository productRepository;

    // 상품 목록 조회
    public List<ProductListResponse> getProducts() {

        return productRepository.findAll()   // 전체 상품 목록 조회
                .stream()                    // 리스트 → 스트림 변환
                .map(p -> ProductListResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .price(p.getPrice())
                        .build()
                )
                .toList();
    }

    // 상품 상세 조회
    public ProductDetailResponse getProduct(Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        // 상품이 없으면 예외 발생

        return ProductDetailResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .sellerName(product.getSeller().getName())
                .build();
    }
}
