package bih.iths.sedina.orderservicewebshop.dto;

public record ProductStockRequest(
        Long productId,
        int quantity
) {
}
