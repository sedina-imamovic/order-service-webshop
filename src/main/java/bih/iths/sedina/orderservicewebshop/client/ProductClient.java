package bih.iths.sedina.orderservicewebshop.client;

import bih.iths.sedina.orderservicewebshop.dto.OrderItemRequest;
import bih.iths.sedina.orderservicewebshop.dto.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductClient {

    private final RestClient restClient;

    public List<ProductInfo> decreaseStock(List<OrderItemRequest> items,
                                           String bearerToken) {
        return restClient.post()
                .uri("/products/stock/decrease")
                .header("Authorization", "Bearer" + bearerToken)
                .body(items)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
