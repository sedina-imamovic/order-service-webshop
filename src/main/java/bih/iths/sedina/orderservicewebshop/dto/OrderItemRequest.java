package bih.iths.sedina.orderservicewebshop.dto;

public record OrderItemRequest(
        Long productId,
        int quantity
) {
}
