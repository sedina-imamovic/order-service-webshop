package bih.iths.sedina.orderservicewebshop.controller;

import bih.iths.sedina.orderservicewebshop.dto.CreateOrderRequest;
import bih.iths.sedina.orderservicewebshop.dto.OrderResponseDto;
import bih.iths.sedina.orderservicewebshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(
            @RequestBody CreateOrderRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String bearerToken = "Bearer " + jwt.getTokenValue();

        String customerName = jwt.getSubject();

        OrderResponseDto response = orderService.createOrder(request, customerName, bearerToken);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDto>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

}
