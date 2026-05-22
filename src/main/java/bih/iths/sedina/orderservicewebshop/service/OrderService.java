package bih.iths.sedina.orderservicewebshop.service;

import bih.iths.sedina.orderservicewebshop.client.ProductClient;
import bih.iths.sedina.orderservicewebshop.dto.CreateOrderItemRequest;
import bih.iths.sedina.orderservicewebshop.dto.CreateOrderRequest;
import bih.iths.sedina.orderservicewebshop.dto.ProductInfo;
import bih.iths.sedina.orderservicewebshop.dto.ProductStockRequest;
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

    public Order createOrder(CreateOrderRequest request, Jwt jwt) {

        List<ProductStockRequest> stockRequests =
                request.items()
                        .stream()
                        .map(item -> new ProductStockRequest(
                                item.productId(),
                                item.quantity()))
                        .toList();

        List<ProductInfo> products = productClient.decreaseStock(stockRequests);

        List<OrderItem> orderItems = new ArrayList<>();

        double totalPrice = 0;

        for (int i = 0; i < products.size(); i++) {

            ProductInfo product = products.get(i);

            CreateOrderItemRequest requestItem = request.items().get(i);

            OrderItem orderItem = new OrderItem();

            orderItem.setName(product.name());
            orderItem.setPrice(product.price());
            orderItem.setQuantity(requestItem.quantity());

            orderItems.add(orderItem);

            totalPrice += product.price() * requestItem.quantity();
        }

        Order order = new Order();

        order.setOrderDate(LocalDate.now());
        order.setCustomerName(jwt.getSubject());
        order.setOrderItems(orderItems);
        order.setTotalPrice(totalPrice);

        for (OrderItem item : orderItems) {
            item.setOrder(order);
        }

        Order savedOrder = orderRepository.save(order);

        messagePublisher.publish(savedOrder, jwt.getSubject());

        return savedOrder;
    }
}
