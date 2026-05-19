package bih.iths.sedina.orderservicewebshop.dto;

public record CreateOrderItemRequest(
        Long productId,
        int quantity
) {
}
