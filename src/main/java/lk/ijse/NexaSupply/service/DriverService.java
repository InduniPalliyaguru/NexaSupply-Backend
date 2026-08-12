package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.DriverRequestDTO;
import lk.ijse.NexaSupply.dto.DriverResponseDTO;
import lk.ijse.NexaSupply.enumeration.DriverStatus;

import java.util.List;

public interface DriverService {

    DriverResponseDTO saveDrive(DriverRequestDTO dto);

    DriverResponseDTO getDriverByCode(String driverCode);

    List<DriverResponseDTO> getAllActiveDrivers();

    List<DriverResponseDTO> getDriversByStatus(DriverStatus status);

    DriverResponseDTO updateDriver(String driverCode, DriverRequestDTO dto);

    void updateDriverStatus(String driverCode, DriverStatus status);

    void deleteDriver(String driverCode);

}
