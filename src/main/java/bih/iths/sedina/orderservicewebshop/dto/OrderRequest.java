package bih.iths.sedina.orderservicewebshop.dto;

import java.util.List;

public record OrderRequest(
        List<OrderItemRequest> items
) {
}
