package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.restock.RestockDetailResponseDTO;
import lk.ijse.NexaSupply.dto.restock.RestockItemDTO;
import lk.ijse.NexaSupply.dto.restock.RestockRequestDTO;
import lk.ijse.NexaSupply.dto.restock.RestockResponseDTO;
import lk.ijse.NexaSupply.entity.Product;
import lk.ijse.NexaSupply.entity.Restock;
import lk.ijse.NexaSupply.entity.RestockDetail;
import lk.ijse.NexaSupply.entity.Supplier;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.ProductRepository;
import lk.ijse.NexaSupply.repository.RestockDetailRepository;
import lk.ijse.NexaSupply.repository.RestockRepository;
import lk.ijse.NexaSupply.repository.SupplierRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.RestockService;
import lk.ijse.NexaSupply.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestockServiceImpl implements RestockService {

    private final RestockRepository restockRepository;
    private final RestockDetailRepository restockDetailRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public RestockResponseDTO addRestock(RestockRequestDTO dto) {
        log.info("Execute addRestock method");

        Optional<Supplier> supplierOptional = supplierRepository.findBySupplierCodeAndDataStatus(dto.getSupplierCode(), DataStatus.ACTIVE);
        if (supplierOptional.isEmpty()) {
            log.error("Active Supplier Not Found: {}", dto.getSupplierCode());
            throw new CustomException(404, "Active Supplier Not Found");
        }
        Supplier supplier = supplierOptional.get();

        Restock restock = new Restock();
        restock.setRestockCode(generateRestockCode());
        restock.setInvoiceNumber(dto.getInvoiceNumber());
        restock.setRestockDate(LocalDateTime.now());
        restock.setSupplier(supplier);

        Restock savedRestock = restockRepository.save(restock);

        double totalCost = 0;
        List<RestockDetail> detailList = new ArrayList<>();
        List<RestockItemDTO> itemsList = dto.getItems();

        for (RestockItemDTO item : itemsList) {

            Optional<Product> optionalProduct = productRepository.findActiveByProductCode(item.getProductCode());
            if (optionalProduct.isEmpty()) {
                log.error("Product Not Found: {}", item.getProductCode());
                throw new CustomException(404, "Product Not Found: " + item.getProductCode());
            }
            Product product = optionalProduct.get();

            double subTotal = item.getQtyAdded() * item.getPurchaseUnitPrice();
            totalCost += subTotal;

            int newQty = product.getQuantity() + item.getQtyAdded();
            product.setQuantity(newQty);
            productRepository.save(product);

            RestockDetail restockDetail = new RestockDetail();
            restockDetail.setRestock(savedRestock);
            restockDetail.setProduct(product);
            restockDetail.setQtyAdded(item.getQtyAdded());
            restockDetail.setPurchaseUnitPrice(item.getPurchaseUnitPrice());
            restockDetail.setSubTotal(subTotal);

            RestockDetail savedRestockDetail = restockDetailRepository.save(restockDetail);
            detailList.add(savedRestockDetail);
        }

        savedRestock.setTotalCost(totalCost);
        savedRestock.setRestockDetails(detailList);
        restockRepository.save(savedRestock);

        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(),
                "RESTOCK_ADDED | Code: " + savedRestock.getRestockCode() + " | Supplier: " + supplier.getSupplierCode() + " | Cost: " + totalCost);

        return mapToResponseDTO(savedRestock);
    }

    @Override
    public List<RestockResponseDTO> getAllRestockHistory() {
        log.info("Execute getAllRestockHistory method");

        List<Restock> all = restockRepository.findAllByOrderByRestockDateDesc();
        List<RestockResponseDTO> detailList = new ArrayList<>();
        for (Restock restock : all) {
            RestockResponseDTO responseDTO = mapToResponseDTO(restock);
            detailList.add(responseDTO);
        }
        return detailList;
    }

    @Override
    public RestockResponseDTO getRestockByCode(String restockCode) {
        log.info("Execute getRestockByCode method");

        Optional<Restock> byRestockCode = restockRepository.findByRestockCode(restockCode);
        if (byRestockCode.isEmpty()) {
            log.error("Restock Code Not Found:{}", restockCode);
            throw new CustomException(404, "Restock Not Found: " + restockCode);
        }

        Restock restock = byRestockCode.get();
        return mapToResponseDTO(restock);
    }

    private String generateRestockCode() {
        int year = Year.now().getValue();
        long count = restockRepository.countAllRestocks() + 1;
        return String.format("RST-%d-%04d", year, count);
    }

    private RestockResponseDTO mapToResponseDTO(Restock restock) {
        List<RestockDetailResponseDTO> details = new ArrayList<>();

        for (RestockDetail d : restock.getRestockDetails()) {
            RestockDetailResponseDTO detailDTO = new RestockDetailResponseDTO(
                    d.getProduct().getProductCode(),
                    d.getProduct().getName(),
                    d.getQtyAdded(),
                    d.getPurchaseUnitPrice(),
                    d.getSubTotal()
            );
            details.add(detailDTO);
        }

        return new RestockResponseDTO(
                restock.getRestockCode(),
                restock.getSupplier().getCompanyName(),
                restock.getInvoiceNumber(),
                restock.getRestockDate(),
                restock.getTotalCost(),
                details
        );
    }
}
