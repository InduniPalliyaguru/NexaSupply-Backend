package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.AdminDashboardDTO;
import lk.ijse.NexaSupply.dto.RetailerDashboardDTO;

public interface DashboardService {

    AdminDashboardDTO getAdminDashboardData();

    RetailerDashboardDTO getRetailerDashboardData();

}
