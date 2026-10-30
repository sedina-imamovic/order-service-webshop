package bih.iths.sedina.orderservicewebshop.repository;

import bih.iths.sedina.orderservicewebshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Hämtar alla ordrar som tillhör en specifik kund
    List<Order> findByCustomerName(String customerName);
}
