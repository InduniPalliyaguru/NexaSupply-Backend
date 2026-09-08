package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.supplier.SupplierDTO;
import lk.ijse.NexaSupply.entity.Supplier;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.SupplierRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.SupplierService;
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
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final AuditLogService auditLogService;

    @Override
    public SupplierDTO createSupplier(SupplierDTO dto) {
        log.info("Execute create supplier method");

        Supplier supplier = new Supplier();

        supplier.setSupplierCode(generateSupplierCode());
        supplier.setCompanyName(dto.getCompanyName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());
        supplier.setDataStatus(DataStatus.ACTIVE);

        Supplier saved = supplierRepository.save(supplier);

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), "CREATED_SUPPLIER | Code: " + saved.getSupplierCode() + " | Company: " + saved.getCompanyName());

        return mapToDTO(saved);
    }

    @Override
    public SupplierDTO updateSupplier(SupplierDTO dto) {
        log.info("Execute update supplier method");

        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCodeAndDataStatus(dto.getSupplierCode(), DataStatus.ACTIVE);
        if (supplierOptional.isEmpty()) {
            log.error("Supplier not found");
            throw new CustomException(404, "Supplier Not Found with Code: " + dto.getSupplierCode());
        }
        Supplier supplier = supplierOptional.get();
        supplier.setCompanyName(dto.getCompanyName());
        supplier.setContactPerson(dto.getContactPerson());
        supplier.setPhone(dto.getPhone());
        supplier.setEmail(dto.getEmail());
        supplier.setAddress(dto.getAddress());

        Supplier updated = supplierRepository.save(supplier);

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), "UPDATED_SUPPLIER | Code: " + dto.getSupplierCode());

        return mapToDTO(updated);
    }

    @Override
    public void deleteSupplier(String supplierCode) {
        log.info("Execute delete supplier method");

        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCodeAndDataStatus(supplierCode, DataStatus.ACTIVE);
        if (supplierOptional.isEmpty()) {
            log.error("Supplier not found with Code: {}", supplierCode);
            throw new CustomException(404, "Supplier Not Found with Code: " + supplierCode);
        }
        Supplier supplier = supplierOptional.get();
        supplier.setDataStatus(DataStatus.INACTIVE);

        supplierRepository.save(supplier);

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), "DELETED_SUPPLIER | Code: " + supplierCode);
    }

    @Override
    public List<SupplierDTO> getAllActiveSuppliers() {
        log.info("Execute getAllActiveSuppliers method");

        List<Supplier> all = supplierRepository.findAllByDataStatus(DataStatus.ACTIVE);

        List<SupplierDTO> dtoList = new ArrayList<>();
        for (Supplier supplier : all) {
            SupplierDTO mapped = mapToDTO(supplier);
            dtoList.add(mapped);
        }
        return dtoList;
    }

    @Override
    public SupplierDTO getSupplierByCode(String supplierCode) {
        log.info("Execute getSupplierByCode method");

        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCodeAndDataStatus(supplierCode, DataStatus.ACTIVE);
        if (supplierOptional.isEmpty()) {
            log.error("Supplier not found with Code : {}", supplierCode);
            throw new CustomException(404, "Supplier Not Found with Code: " + supplierCode);
        }
        Supplier supplier = supplierOptional.get();
        return mapToDTO(supplier);
    }

    @Override
    public List<SupplierDTO> searchSuppliers(String query) {
        log.info("Execute searchSuppliers method");

        List<Supplier> suppliers = supplierRepository.searchSuppliers(query);
        List<SupplierDTO> dtoList = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            SupplierDTO mapped = mapToDTO(supplier);
            dtoList.add(mapped);
        }
        return dtoList;
    }

    private String generateSupplierCode() {
        int year = Year.now().getValue();
        long count = supplierRepository.countAllSuppliers() + 1;
        return String.format("SUP-%d-%04d", year, count);
    }

    private SupplierDTO mapToDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getSupplierId(),
                supplier.getSupplierCode(),
                supplier.getCompanyName(),
                supplier.getContactPerson(),
                supplier.getPhone(),
                supplier.getEmail(),
                supplier.getAddress()
        );
    }


}
