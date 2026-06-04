package bih.iths.sedina.orderservicewebshop.service;

import bih.iths.sedina.orderservicewebshop.client.ProductClient;
import bih.iths.sedina.orderservicewebshop.dto.OrderItemRequest;
import bih.iths.sedina.orderservicewebshop.dto.OrderRequest;
import bih.iths.sedina.orderservicewebshop.dto.ProductInfo;
import bih.iths.sedina.orderservicewebshop.model.Order;
import bih.iths.sedina.orderservicewebshop.model.OrderItem;
import bih.iths.sedina.orderservicewebshop.publisher.MessagePublisher;
import bih.iths.sedina.orderservicewebshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final MessagePublisher messagePublisher;

    public Order createOrder(OrderRequest request, String customerName, String bearerToken) {

        List<OrderItemRequest> orderItemRequests = request.items().stream()
                .map(item -> new OrderItemRequest(item.productId(), item.quantity()))
                .toList();

        List<ProductInfo> products = productClient.decreaseStock(orderItemRequests, bearerToken);

        List<OrderItem> orderItems = new ArrayList<>();

        for (ProductInfo product : products) {

            OrderItem orderItem = new OrderItem();

            orderItem.setName(product.name());
            orderItem.setPrice(BigDecimal.valueOf(product.price().doubleValue()));
            orderItem.setQuantity(product.quantity());
            orderItems.add(orderItem);

        }

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setCustomerName(customerName);
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice(orderItems));

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);

        messagePublisher.publish(savedOrder);

        return savedOrder;
    }

    private BigDecimal totalPrice(List<OrderItem> orderItems) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            totalPrice = totalPrice.add(orderItem.getPrice()).multiply(new BigDecimal(orderItem.getQuantity()));
        }
        return totalPrice;
    }


}
