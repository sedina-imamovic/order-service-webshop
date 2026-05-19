package bih.iths.sedina.orderservicewebshop.repository;

import bih.iths.sedina.orderservicewebshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
