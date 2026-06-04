package bih.iths.sedina.orderservicewebshop.confirmation;

import java.util.List;

public record ConfirmationOrderMessage(
        String customerName,
        List<OrderItemsMessage> items,
        double totalPrice
) {
}
