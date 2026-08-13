package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.MonthlySalesDTO;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.OrderStatus;
import lk.ijse.NexaSupply.enumeration.Role;
import lk.ijse.NexaSupply.repository.OrderRepository;
import lk.ijse.NexaSupply.repository.ProductRepository;
import lk.ijse.NexaSupply.repository.UserRepository;
import lk.ijse.NexaSupply.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
