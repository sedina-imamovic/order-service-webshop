package bih.iths.sedina.orderservicewebshop.service;

import bih.iths.sedina.orderservicewebshop.client.ProductClient;
import bih.iths.sedina.orderservicewebshop.confirmation.ConfirmationOrderMessage;
import bih.iths.sedina.orderservicewebshop.confirmation.OrderItemsMessage;
import bih.iths.sedina.orderservicewebshop.dto.*;
import bih.iths.sedina.orderservicewebshop.model.Order;
import bih.iths.sedina.orderservicewebshop.model.OrderItem;
import bih.iths.sedina.orderservicewebshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;


    // Metod för att skapa en order baserat på en CreateOrderRequest, kundnamn och bearer-token
    public OrderResponseDto createOrder(CreateOrderRequest request, String customerName, String bearerToken) {

        // Skapar en lista av OrderItemRequest baserat på CreateOrderRequest
        List<OrderItemRequest> orderItemRequests = request.items().stream()
                .map(item -> new OrderItemRequest(item.productId(), item.quantity()))
                .toList();

        List<ProductInfo> products = productClient.decreaseStock(orderItemRequests, bearerToken);

        List<OrderItem> orderItems = new ArrayList<>();

        // Loopar igenom produkterna och skapar OrderItem-objekt baserat på produktinformation och kvantitet
        for (int i = 0; i < products.size(); i++) {
            ProductInfo Info = products.get(i);
            int quan = request.items().get(i).quantity();

            OrderItem item = new OrderItem();
            item.setName(Info.name());
            item.setPrice(Info.price());
            item.setQuantity(quan);
            orderItems.add(item);

        }

        // Skapar en Order, sätter orderdatum, kundnamn och orderitems, samt beräknar totalpriset
        Order order = new Order();
        order.setOrderDate(LocalDate.now());
        order.setCustomerName(customerName);
        orderItems.forEach(item -> item.setOrder(order));
        order.setOrderItems(orderItems);

        order.setTotalPrice(orderItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity()).sum());


        Order savedOrder = orderRepository.save(order);

        // Skapar en lista av OrderItemsMessage baserat på sparade orderitems
        List<OrderItemsMessage> messageItems = savedOrder.getOrderItems().stream()
                .map(i -> new OrderItemsMessage(i.getName(), i.getQuantity(), i.getPrice()))
                .toList();

        //  Skapar ett ConfirmationOrderMessage med kundnamn, orderitems och totalpris, och skickar det till RabbitMQ
        ConfirmationOrderMessage message = new ConfirmationOrderMessage(
                customerName,
                messageItems,
                savedOrder.getTotalPrice());

        rabbitTemplate.convertAndSend("email-queue", message);
        
        return sendOrder(savedOrder);

    }

    // Metod för att konvertera en Order till en OrderResponseDto
    private OrderResponseDto sendOrder(Order order) {
        List<OrderItemResponseDto> items = order.getOrderItems().stream()
                .map(i -> new OrderItemResponseDto(i.getName(), i.getPrice(), i.getQuantity()))
                .toList();

        // Skapar och returnerar en OrderResponseDto med orderinformation och orderitems
        return new OrderResponseDto(
                order.getId(),
                order.getOrderDate(),
                order.getCustomerName(),
                order.getTotalPrice(),
                items
        );
    }

    // Metod för att hämta alla orders och konvertera dem till en lista av OrderResponseDto
    public List<OrderResponseDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::sendOrder)
                .toList();
    }

    // Hämtar alla ordrar som tillhör en specifik kund
    public List<OrderResponseDto> getOrdersByCustomerName(String customerName) {
        return orderRepository.findByCustomerName(customerName).stream()
                .map(this::sendOrder)
                .toList();
    }

}
