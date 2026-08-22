package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.driver.DriverRequestDTO;
import lk.ijse.NexaSupply.dto.driver.DriverResponseDTO;
import lk.ijse.NexaSupply.enumeration.DriverStatus;
import lk.ijse.NexaSupply.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse createDriver(@Valid @RequestBody DriverRequestDTO dto) {
        DriverResponseDTO created = driverService.saveDrive(dto);
        return new CommonResponse(201, created, "Driver created successfully!");
    }

    @PutMapping(value = "/{driverCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse updateDriver(@PathVariable String driverCode, @Valid @RequestBody DriverRequestDTO dto) {
        DriverResponseDTO updated = driverService.updateDriver(driverCode, dto);
        return new CommonResponse(200, updated, "Driver updated successfully!");
    }

    @PutMapping(value = "/{driverCode}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse updateDriverStatus(@PathVariable String driverCode, @RequestParam DriverStatus status) {
        driverService.updateDriverStatus(driverCode, status);
        return new CommonResponse(200, "Driver status updated successfully!");
    }

    @DeleteMapping(value = "/{driverCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse deleteDriver(@PathVariable String driverCode) {
        driverService.deleteDriver(driverCode);
        return new CommonResponse(200, "Driver deleted successfully!");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllActiveDrivers() {
        List<DriverResponseDTO> drivers = driverService.getAllActiveDrivers();
        return new CommonResponse(200, drivers, "Driver list retrieved successfully!");
    }

    @GetMapping(value = "/{driverCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDriverByCode(@PathVariable String driverCode) {
        DriverResponseDTO driver = driverService.getDriverByCode(driverCode);
        return new CommonResponse(200, driver, "Driver details fetched successfully!");
    }

    @GetMapping(value = "/status/{status}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getDriversByStatus(@PathVariable DriverStatus status) {
        List<DriverResponseDTO> drivers = driverService.getDriversByStatus(status);
        return new CommonResponse(200, drivers, "Drivers fetched by status successfully!");
    }

}
