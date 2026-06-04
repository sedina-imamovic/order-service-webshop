package bih.iths.sedina.orderservicewebshop.confirmation;

public record OrderItemsMessage(
        String name,
        int quantity,
        double price
) {
}
