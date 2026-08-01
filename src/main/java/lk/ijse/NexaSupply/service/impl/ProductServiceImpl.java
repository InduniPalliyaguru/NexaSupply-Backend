package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.ProductRequestDTO;
import lk.ijse.NexaSupply.dto.ProductResponseDTO;
import lk.ijse.NexaSupply.entity.Category;
import lk.ijse.NexaSupply.entity.Product;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.CategoryRepository;
import lk.ijse.NexaSupply.repository.ProductRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.ProductService;
import lk.ijse.NexaSupply.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO request) {
        log.info("Execute createProduct Method");

        Optional<Category> category = categoryRepository.findActiveByCategoryCode(request.getCategoryCode());
        if (category.isEmpty()) {
            throw new CustomException(404, "Category not found");
        }
        long count = productRepository.countAllProducts() + 1;
        String productCode = String.format("PR-%04d", count);

        Product product = new Product();
        product.setProductCode(productCode);
        product.setName(request.getName());
        product.setUnitPrice(request.getUnitPrice());
        product.setQuantity(request.getAvailableQty());
        product.setUnit(request.getUnit());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setDataStatus(DataStatus.ACTIVE);
        product.setImgUrl(request.getImgUrl());
        product.setCategory(category.get());

        Product savedProduct = productRepository.save(product);

        String action = "CREATED_PRODUCT | Code: " + savedProduct.getProductCode() + " | Name: " + savedProduct.getName() + " | Qty: " + savedProduct.getQuantity();
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);

        return mapToResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO updateProduct(ProductRequestDTO request) {
        log.info("Execute updateProduct Method");

        Optional<Product> optionalProduct = productRepository.findActiveByProductCode(request.getProductCode());
        if (optionalProduct.isEmpty()) {
            throw new CustomException(404, "Product not found");
        }

        Optional<Category> optionalCategory = categoryRepository.findActiveByCategoryCode(request.getCategoryCode());
        if (optionalCategory.isEmpty()) {
            throw new CustomException(404, "Category not found");
        }

        Product product = optionalProduct.get();

        double oldPrice = product.getUnitPrice();
        int oldQty = product.getQuantity();

        product.setName(request.getName());
        product.setUnitPrice(request.getUnitPrice());
        product.setQuantity(request.getAvailableQty());
        product.setUnit(request.getUnit());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setImgUrl(request.getImgUrl());
        product.setCategory(optionalCategory.get());

        Product updatedProduct = productRepository.save(product);

        String action = "UPDATED_PRODUCT | Code: " + product.getProductCode() + " | Price: [" + oldPrice + " -> " + updatedProduct.getUnitPrice() + "] | Stock: [" + oldQty + " -> " + updatedProduct.getQuantity() + "]";
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);

        return mapToResponseDTO(updatedProduct);
    }

    @Override
    public ProductResponseDTO getProductByCode(String productCode) {
        log.info("Execute getProductByCode Method");

        Optional<Product> optionalProduct = productRepository.findActiveByProductCode(productCode);
        if (optionalProduct.isEmpty()) {
            throw new CustomException(404, "Product not found");
        }
        Product product = optionalProduct.get();
        return mapToResponseDTO(product);
    }

    @Override
    public List<ProductResponseDTO> getAllActiveProducts() {
        log.info("Execute getAllActiveProducts Method");

        List<Product> activeProducts = productRepository.findAllActiveProducts();
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for (Product product : activeProducts) {
            ProductResponseDTO dto = mapToResponseDTO(product);
            productResponseDTOList.add(dto);
        }
        return productResponseDTOList;
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(String categoryCode) {
        log.info("Execute getProductsByCategory Method");

        List<Product> byCategoryCode = productRepository.findActiveByCategoryCode(categoryCode);
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for (Product product : byCategoryCode) {
            ProductResponseDTO dto = mapToResponseDTO(product);
            productResponseDTOList.add(dto);
        }
        return productResponseDTOList;
    }

    @Override
    public List<ProductResponseDTO> getLowStockProducts() {
        log.info("Execute getLowStockProducts Method");

        List<Product> lowStockProducts = productRepository.findLowStockProducts();
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for (Product product : lowStockProducts) {
            ProductResponseDTO dto = mapToResponseDTO(product);
            productResponseDTOList.add(dto);
        }
        return productResponseDTOList;
    }

    @Override
    public List<ProductResponseDTO> searchProducts(String query) {
        log.info("Execute searchProducts Method");

        List<Product> products = productRepository.searchActiveProducts(query);
        List<ProductResponseDTO> productResponseDTOList = new ArrayList<>();

        for (Product product : products) {
            ProductResponseDTO dto = mapToResponseDTO(product);
            productResponseDTOList.add(dto);
        }
        return productResponseDTOList;
    }

    @Override
    public void deleteProduct(String productCode) {
        log.info("Execute deleteProduct Method");

        Optional<Product> optionalProduct = productRepository.findActiveByProductCode(productCode);
        if (optionalProduct.isEmpty()) {
            throw new CustomException(404, "Product not found");
        }

        Product product = optionalProduct.get();
        product.setDataStatus(DataStatus.INACTIVE);
        productRepository.save(product);

        String action = "DELETED_PRODUCT | Code: " + productCode + " | Name: " + product.getName();
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        return new ProductResponseDTO(
                product.getProductId(),
                product.getProductCode(),
                product.getName(),
                product.getUnitPrice(),
                product.getQuantity(),
                product.getUnit(),
                product.getMinStockLevel(),
                product.getImgUrl(),
                product.getCategory().getCategoryCode(),
                product.getCategory().getName()
        );
    }

}
