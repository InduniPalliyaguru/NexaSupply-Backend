package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.driver.DriverRequestDTO;
import lk.ijse.NexaSupply.dto.driver.DriverResponseDTO;
import lk.ijse.NexaSupply.entity.Driver;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.enumeration.DriverStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.DriverRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.DriverService;
import lk.ijse.NexaSupply.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final AuditLogService auditLogService;

    @Override
    public DriverResponseDTO saveDrive(DriverRequestDTO dto) {
        log.info("Execute saveDrive method");

        if (driverRepository.existsByPhoneAndDataStatus(dto.getPhone(), DataStatus.ACTIVE)) {
            throw new CustomException(400, "Phone number already exists!");
        }
        if (driverRepository.existsByLicenseNoAndDataStatus(dto.getLicenseNo(), DataStatus.ACTIVE)) {
            throw new CustomException(400, "License number already exists!");
        }

        Driver driver = new Driver();
        driver.setDriverCode(generateDriverCode());
        driver.setDriverName(dto.getDriverName());
        driver.setPhone(dto.getPhone());
        driver.setLicenseNo(dto.getLicenseNo());
        driver.setDriverStatus(DriverStatus.AVAILABLE);
        driver.setDataStatus(DataStatus.ACTIVE);

        Driver savedDriver = driverRepository.save(driver);

        String action = "CREATED_DRIVER | Code: " + savedDriver.getDriverCode() + " | Name: " + savedDriver.getDriverName() + " | Phone: " + savedDriver.getPhone();
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);

        return mapToDTO(savedDriver);
    }

    @Override
    public DriverResponseDTO getDriverByCode(String driverCode) {
        log.info("Execute getDriverByCode method");

        Optional<Driver> optionalDriver = driverRepository.findByDriverCode(driverCode);
        if (optionalDriver.isEmpty()) {
            throw new CustomException(404, "Driver not found with code: " + driverCode);
        }
        Driver driver = optionalDriver.get();

        return mapToDTO(driver);
    }

    @Override
    public List<DriverResponseDTO> getAllActiveDrivers() {
        log.info("Execute getAllActiveDrivers method");

        List<Driver> activeDrivers = driverRepository.findAllActiveDrivers();
        List<DriverResponseDTO> dtoList = new ArrayList<>();

        for (Driver driver : activeDrivers) {
            DriverResponseDTO mapped = mapToDTO(driver);
            dtoList.add(mapped);
        }
        return dtoList;
    }

    @Override
    public List<DriverResponseDTO> getDriversByStatus(DriverStatus status) {
        log.info("Execute getDriversByStatus method");

        List<Driver> list = driverRepository.findByDriverStatus(status);
        List<DriverResponseDTO> dtoList = new ArrayList<>();

        for (Driver driver : list) {
            DriverResponseDTO mapped = mapToDTO(driver);
            dtoList.add(mapped);
        }
        return dtoList;
    }

    @Override
    public DriverResponseDTO updateDriver(String driverCode, DriverRequestDTO dto) {
        log.info("Execute updateDriver method");

        Optional<Driver> optionalDriver = driverRepository.findByDriverCode(driverCode);
        if (optionalDriver.isEmpty()) {
            throw new CustomException(404, "Driver not found with code: " + driverCode);
        }
        Driver driver = optionalDriver.get();

        String oldPhone = driver.getPhone();
        String oldLicense = driver.getLicenseNo();

        driver.setDriverName(dto.getDriverName());
        driver.setPhone(dto.getPhone());
        driver.setLicenseNo(dto.getLicenseNo());

        Driver updatedDriver = driverRepository.save(driver);

        String action = "UPDATED_DRIVER | Code: " + updatedDriver.getDriverCode() + " | Phone: [" + oldPhone + " -> " + updatedDriver.getPhone() + "] | License: [" + oldLicense + " -> " + updatedDriver.getLicenseNo() + "]";
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);

        return mapToDTO(updatedDriver);
    }

    @Override
    public void updateDriverStatus(String driverCode, DriverStatus status) {
        log.info("Execute updateDriverStatus method");

        Optional<Driver> optionalDriver = driverRepository.findByDriverCode(driverCode);
        if (optionalDriver.isEmpty()) {
            throw new CustomException(404, "Driver not found with code: " + driverCode);
        }
        Driver driver = optionalDriver.get();

        DriverStatus oldStatus = driver.getDriverStatus();

        driver.setDriverStatus(status);
        driverRepository.save(driver);

        String action = "UPDATED_DRIVER_STATUS | Code: " + driverCode + " | Status: [" + oldStatus + " -> " + status + "]";
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);
    }

    @Override
    public void deleteDriver(String driverCode) {
        log.info("Execute deleteDriver method");

        Optional<Driver> optionalDriver = driverRepository.findByDriverCode(driverCode);
        if (optionalDriver.isEmpty()) {
            throw new CustomException(404, "Driver not found with code: " + driverCode);
        }
        Driver driver = optionalDriver.get();
        driver.setDataStatus(DataStatus.INACTIVE);

        String action = "DELETED_DRIVER | Code: " + driverCode + " | Name: " + driver.getDriverName();
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);
    }

    private String generateDriverCode() {
        int year = Year.now().getValue();
        long count = driverRepository.countAllDrivers() + 1;
        return String.format("DRV-%d-%04d", year, count);
    }

    private DriverResponseDTO mapToDTO(Driver driver) {
        DriverResponseDTO dto = new DriverResponseDTO();
        dto.setDriverCode(driver.getDriverCode());
        dto.setDriverName(driver.getDriverName());
        dto.setPhone(driver.getPhone());
        dto.setLicenseNo(driver.getLicenseNo());
        dto.setStatus(driver.getDriverStatus());
        return dto;
    }

}
