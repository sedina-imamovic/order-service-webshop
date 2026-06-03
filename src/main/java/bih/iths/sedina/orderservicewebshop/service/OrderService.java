package bih.iths.sedina.orderservicewebshop.service;

import bih.iths.sedina.orderservicewebshop.client.ProductClient;
import bih.iths.sedina.orderservicewebshop.dto.OrderRequest;
import bih.iths.sedina.orderservicewebshop.dto.ProductInfo;
import bih.iths.sedina.orderservicewebshop.model.Order;
import bih.iths.sedina.orderservicewebshop.model.OrderItem;
import bih.iths.sedina.orderservicewebshop.publisher.MessagePublisher;
import bih.iths.sedina.orderservicewebshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final MessagePublisher messagePublisher;

    public Order createOrder(OrderRequest request, Jwt jwt) {

        List<ProductInfo> products = productClient.decreaseStock(request.items(), jwt.getTokenValue());

        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setCustomerName(jwt.getSubject());

        List<OrderItem> orderItems = new ArrayList<>();

        double totalPrice = 0;

        for (ProductInfo product : products) {

            OrderItem orderItem = new OrderItem();

            orderItem.setName(product.name());
            orderItem.setPrice(product.price().doubleValue());
            orderItem.setQuantity(product.quantity());

            orderItems.add(orderItem);

            totalPrice += product.price().doubleValue() * product.quantity();
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);

        messagePublisher.publish(savedOrder);

        return savedOrder;
    }
}
