package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.dto.MonthlySalesDTO;
import lk.ijse.NexaSupply.entity.Order;
import lk.ijse.NexaSupply.enumeration.OrderStatus;
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

    long countByOrderStatus(OrderStatus orderStatus);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o WHERE o.orderStatus != 'CANCELLED'")
    double sumTotalRevenue();

    @Query("SELECT new lk.ijse.NexaSupply.dto.MonthlySalesDTO(" +
            "FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%b'), " +
            "COALESCE(SUM(o.totalPrice), 0.0)) " +
            "FROM Order o " +
            "WHERE o.orderStatus != ?1 " +
            "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate), FUNCTION('DATE_FORMAT', o.orderDate, '%Y-%b') " +
            "ORDER BY YEAR(o.orderDate) ASC, MONTH(o.orderDate) ASC")
    List<MonthlySalesDTO> findMonthlySalesRaw(OrderStatus orderStatus);

    long countByCustomer_EmailAndOrderStatus(String email, OrderStatus orderStatus);

    long countByCustomer_Email(String email);

    @Query("SELECT COALESCE(SUM(o.totalPrice), 0.0) FROM Order o WHERE o.customer.email = ?1 AND o.orderStatus != ?2")
    double sumTotalSpentByCustomer(String email, OrderStatus orderStatus);

    @Query(value = "SELECT o.* FROM orders o " +
            "INNER JOIN user u ON o.customer_user_id = u.user_id " +
            "WHERE u.email = ?1 " +
            "ORDER BY o.order_date DESC LIMIT 5", nativeQuery = true)
    List<Order> findRecentOrders(String email);

}
