package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.dashboard.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.dashboard.RetailerDashboardDTO;

public interface DashboardService {

    AdminDashboardDTO getAdminDashboardData();

    RetailerDashboardDTO getRetailerDashboardData();

}
