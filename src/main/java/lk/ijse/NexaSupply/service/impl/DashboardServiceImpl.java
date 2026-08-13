package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.MonthlySalesDTO;
import lk.ijse.NexaSupply.dto.RecentOrderDTO;
import lk.ijse.NexaSupply.dto.RetailerDashboardDTO;
import lk.ijse.NexaSupply.entity.Order;
import lk.ijse.NexaSupply.entity.User;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.OrderRepository;
import lk.ijse.NexaSupply.repository.ProductRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.DashboardService;
import lk.ijse.NexaSupply.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public AdminDashboardDTO getAdminDashboardData() {
        log.info("Execute getAdminDashboardData Method");

        double totalRevenue = orderRepository.sumTotalRevenue();
        long pendingOrderCount = orderRepository.countByOrderStatus(OrderStatus.PENDING);
        long lowStockProducts = productRepository.countLowStockProducts();
        long activeRetailerCount = userRepository.countByRoleAndDataStatus(Role.ROLE_RETAILER, DataStatus.ACTIVE);
        List<MonthlySalesDTO> monthlySalesRaw = orderRepository.findMonthlySalesRaw(OrderStatus.CANCELLED);

        return new AdminDashboardDTO(
                totalRevenue,
                pendingOrderCount,
                lowStockProducts,
                activeRetailerCount,
                monthlySalesRaw
        );

    }

    @Override
    public RetailerDashboardDTO getRetailerDashboardData() {
        log.info("Execute getRetailerDashboardData Method");

        String currentUserEmail = SecurityUtils.getCurrentUserEmail();
        Optional<User> optionalUser = userRepository.findActiveByEmail(currentUserEmail);
        if (optionalUser.isEmpty()) {
            throw new CustomException(404, "Retailer account not found!");
        }
        User retailer = optionalUser.get();
        double availableCreditLimit = retailer.getCreditLimit();
        long pendingOrderCount = orderRepository.countByCustomer_EmailAndOrderStatus(currentUserEmail, OrderStatus.PENDING);
        long totalOrderCount = orderRepository.countByCustomer_Email(currentUserEmail);
        double totalSpentAmount = orderRepository.sumTotalSpentByCustomer(currentUserEmail, OrderStatus.CANCELLED);

        List<Order> orderList = orderRepository.findRecentOrders(currentUserEmail);
        List<RecentOrderDTO> recentOrderDTOList = new ArrayList<>();

        for (Order order : orderList) {
            RecentOrderDTO recentOrderDTO = new RecentOrderDTO();
            recentOrderDTO.setOrderCode(order.getOrderCode());
            recentOrderDTO.setOrderDate(order.getOrderDate());
            recentOrderDTO.setTotalPrice(order.getTotalPrice());
            recentOrderDTO.setOrderStatus(order.getOrderStatus());
            recentOrderDTOList.add(recentOrderDTO);
        }
        return new RetailerDashboardDTO(
                availableCreditLimit,
                pendingOrderCount,
                totalOrderCount,
                totalSpentAmount,
                recentOrderDTOList
        );

    }
}
