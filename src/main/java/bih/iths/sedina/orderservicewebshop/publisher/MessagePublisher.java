package bih.iths.sedina.orderservicewebshop.publisher;

import bih.iths.sedina.orderservicewebshop.config.RabbitConfig;
import bih.iths.sedina.orderservicewebshop.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(Order order, String email) {

        Map<String, Object> message = new HashMap<>();

        message.put("orderId", order.getId());
        message.put("email", email);
        message.put("customerName", order.getCustomerName());
        message.put("orderDate", order.getOrderDate());
        message.put("totalPrice", order.getTotalPrice());

        message.put("items",
                order.getOrderItems().stream()
                        .map(item -> Map.of(
                                "productId", item.getId(),
                                "name", item.getName(),
                                "price", item.getPrice(),
                                "quantity", item.getQuantity()))
                        .toList());

        rabbitTemplate.convertAndSend(RabbitConfig.QUEUE, message);
    }
}
