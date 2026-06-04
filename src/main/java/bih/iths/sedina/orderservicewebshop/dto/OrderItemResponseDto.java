package bih.iths.sedina.orderservicewebshop.dto;

public record OrderItemResponseDto(
        String name,
        double price,
        int quantity
) {
}
