package lk.ijse.NexaSupply.controller;

import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.dashboard.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.dashboard.RetailerDashboardDTO;
import lk.ijse.NexaSupply.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping(value = "/admin", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getAdminDashboardData() {
        AdminDashboardDTO adminDashboardData = dashboardService.getAdminDashboardData();
        return new CommonResponse(200, adminDashboardData, "Admin dashboard data fetched successfully!");
    }

    @GetMapping(value = "/retailer", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_RETAILER')")
    public CommonResponse getRetailerDashboardData() {
        RetailerDashboardDTO retailerDashboardData = dashboardService.getRetailerDashboardData();
        return new CommonResponse(200, retailerDashboardData, "Retailer dashboard data fetched successfully!");
    }

}
