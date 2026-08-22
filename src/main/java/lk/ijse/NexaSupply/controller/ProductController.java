package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.product.ProductRequestDTO;
import lk.ijse.NexaSupply.dto.product.ProductResponseDTO;
import lk.ijse.NexaSupply.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse createProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO product = productService.createProduct(requestDTO);
        return new CommonResponse(201, product, "Product created successfully");
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse updateProduct(@Valid @RequestBody ProductRequestDTO requestDTO) {
        ProductResponseDTO dto = productService.updateProduct(requestDTO);
        return new CommonResponse(200, dto, "Product updated successfully");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllProducts() {
        List<ProductResponseDTO> allActiveProducts = productService.getAllActiveProducts();
        return new CommonResponse(200, allActiveProducts, "All active products successfully");
    }

    @GetMapping(value = "/{productCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getProductByCode(@PathVariable String productCode) {
        ProductResponseDTO productByCode = productService.getProductByCode(productCode);
        return new CommonResponse(200, productByCode, "Product fetched successfully");
    }

    @GetMapping(value = "/category/{categoryCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getProductsByCategory(@PathVariable String categoryCode) {
        List<ProductResponseDTO> productsByCategory = productService.getProductsByCategory(categoryCode);
        return new CommonResponse(200, productsByCategory, "All products fetched successfully");
    }

    @GetMapping(value = "/low-stock", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse getProductsByLowStock() {
        List<ProductResponseDTO> lowStockProducts = productService.getLowStockProducts();
        return new CommonResponse(200, lowStockProducts, "All low stock products fetched successfully");
    }

    @GetMapping(value = "/search", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse searchProducts(@RequestParam String query) {
        List<ProductResponseDTO> searchResults = productService.searchProducts(query);
        return new CommonResponse(200, searchResults, "Search results fetched successfully");
    }

    @DeleteMapping(value = "/{productCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse deleteProduct(@PathVariable String productCode) {
        productService.deleteProduct(productCode);
        return new CommonResponse(200, "Product deleted successfully", "Successfully deleted product");
    }

}
