package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.SupplierDTO;

import java.util.List;

public interface SupplierService {

    SupplierDTO createSupplier(SupplierDTO dto);

    SupplierDTO updateSupplier(SupplierDTO dto);

    void deleteSupplier(String supplierCode);

    List<SupplierDTO> getAllActiveSuppliers();

}
