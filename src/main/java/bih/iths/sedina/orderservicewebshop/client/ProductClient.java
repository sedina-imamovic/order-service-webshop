package bih.iths.sedina.orderservicewebshop.client;

import bih.iths.sedina.orderservicewebshop.dto.ProductInfo;
import bih.iths.sedina.orderservicewebshop.dto.ProductStockRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient restClient;

    public List<ProductInfo> decreaseStock(List<ProductStockRequest> items) {
        return restClient.post()
                .uri("/products/stock/decrease")
                .body(items)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
