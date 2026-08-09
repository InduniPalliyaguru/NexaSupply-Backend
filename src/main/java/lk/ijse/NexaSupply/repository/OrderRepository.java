package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByCustomer_EmailOrderByOrderDateDesc(String email);

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrdersSortedByDate();

    @Query("SELECT COUNT(o) FROM Order o")
    long countAllOrders();

}
