package bih.iths.sedina.orderservicewebshop.confirmation;

// Record som representerar ett meddelande för en orderartikel, innehållande namn, kvantitet och pris
public record OrderItemsMessage(
        String name,
        int quantity,
        double price
) {
}
