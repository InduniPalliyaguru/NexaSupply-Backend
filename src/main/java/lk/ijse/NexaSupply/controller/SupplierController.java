package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.SupplierDTO;
import lk.ijse.NexaSupply.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse createSupplier(@Valid @RequestBody SupplierDTO dto) {
        SupplierDTO created = supplierService.createSupplier(dto);
        return new CommonResponse(201, created, "Supplier created successfully!");
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse updateSupplier(@Valid @RequestBody SupplierDTO dto) {
        SupplierDTO updated = supplierService.updateSupplier(dto);
        return new CommonResponse(200, updated, "Supplier updated successfully!");
    }

    @DeleteMapping(value = "/{supplierCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse deleteSupplier(@PathVariable String supplierCode) {
        supplierService.deleteSupplier(supplierCode);
        return new CommonResponse(200, "Supplier deleted successfully!");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllActiveSuppliers() {
        List<SupplierDTO> suppliers = supplierService.getAllActiveSuppliers();
        return new CommonResponse(200, suppliers, "Supplier list retrieved successfully!");
    }

    @GetMapping(value = "/{supplierCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getSupplierByCode(@PathVariable String supplierCode) {
        SupplierDTO supplier = supplierService.getSupplierByCode(supplierCode);
        return new CommonResponse(200, supplier, "Supplier details fetched successfully");
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse searchSuppliers(@RequestParam String query) {
        List<SupplierDTO> searchResults = supplierService.searchSuppliers(query);
        return new CommonResponse(200, searchResults, "Search results fetched successfully");
    }

}