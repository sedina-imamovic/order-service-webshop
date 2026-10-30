package bih.iths.sedina.orderservicewebshop.confirmation;

import java.util.List;

// Record som representerar ett meddelande för orderbekräftelse, innehållande kundnamn, en lista av orderartiklar och totalpris
public record ConfirmationOrderMessage(
        String customerName,
        List<OrderItemsMessage> items,
        double totalPrice
) {
}
