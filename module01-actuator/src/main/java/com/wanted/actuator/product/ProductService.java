package com.wanted.actuator.product;

import com.wanted.actuator.metric.ShopMetrics;
import com.wanted.actuator.product.dto.CreateProductRequest;
import com.wanted.actuator.product.dto.PopularProductResponse;
import com.wanted.actuator.product.dto.ProductResponse;
import io.micrometer.core.instrument.Timer;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopMetrics shopMetrics;

    public ProductService(ProductRepository productRepository, ShopMetrics shopMetrics) {
        this.productRepository = productRepository;
        this.shopMetrics = shopMetrics;
    }

    @Transactional
    public ProductResponse createProduct(CreateProductRequest request) {
        Product product = new Product(request.name(), request.price(), request.stock());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public ProductResponse getProduct(Long id) {
        return ProductResponse.from(findProduct(id));
    }

    /* comment.
    *   해당 메서드는 7일간 가장 인기 있는 상품을 반환하는 메서드이다.
    *   가정)
    *   - 해당 메서드는 개발자들이 생각하기에 서비스에서 가장 많이 호출되는 메서드 일 것 같다.
    *   - 개발 완료 후, 개발자는 우리의 생각이 실 사용자들의 데이터와 일치하는 지 확인한다.
    *   - Metric 을 통해서 실 지효 바탕으로 일치하는 지를 판단한다.
    *   - Timer 를 활용해서 해당 메서드가 얼마나 걸리는 지 확인한다.
    *   - 지연시간을 확인하고 성능 최적화에 대한 논의를 시작한다.
    *   - Before / After 를 비교해서 어떤 방식을 채택할 지 논의한다.
    * */
    @Transactional(readOnly = true)
    public List<PopularProductResponse> getPopularProducts() {

        // 얼마나 호출 되는 지를 확인하는 Metric
        shopMetrics.recordPopularProductRequest();

        // 지연 시간을 확인하는 Metric
        Timer.Sample sample = shopMetrics.startTimer();

        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        try {
            return productRepository.findPopularProducts(sevenDaysAgo, PageRequest.of(0, 5));
        } finally {
            // 지연 시간을 확인하는 Metric
            shopMetrics.stopPopularProductQueryTimer(sample);
        }
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
    }
}
