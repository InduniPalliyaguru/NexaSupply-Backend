package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.ProductRequestDTO;
import lk.ijse.NexaSupply.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {

    ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO updateProduct(ProductRequestDTO productRequestDTO);

    ProductResponseDTO getProductByCode(String productCode);

    List<ProductResponseDTO> getAllActiveProducts();

    List<ProductResponseDTO> getProductsByCategory(String categoryCode);

    List<ProductResponseDTO> getLowStockProducts();

    List<ProductResponseDTO> searchProducts(String query);

    void deleteProduct(String productCode);

}
