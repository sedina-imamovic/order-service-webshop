package bih.iths.sedina.orderservicewebshop.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderResponseDto(
        Long id,
        LocalDate orderDate,
        String customerName,
        double totalPrice,
        List<OrderItemResponseDto> orderItems
) {
}
