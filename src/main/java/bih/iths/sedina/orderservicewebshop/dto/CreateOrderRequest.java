package bih.iths.sedina.orderservicewebshop.dto;

import java.util.List;

public record CreateOrderRequest(
        List<OrderItemRequest> items
) {
}
