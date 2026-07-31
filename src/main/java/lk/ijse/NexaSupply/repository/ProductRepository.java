package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.dataStatus = 'ACTIVE'")
    List<Product> findAllActiveProducts();

    @Query("SELECT p FROM Product p WHERE p.productCode = ?1 AND p.dataStatus = 'ACTIVE'")
    Optional<Product> findActiveByProductCode(String productCode);

    @Query("SELECT p FROM Product p WHERE p.category.categoryCode = ?1 AND p.dataStatus = 'ACTIVE'")
    List<Product> findActiveByCategoryCode(String categoryCode);

    @Query("SELECT p FROM Product p WHERE p.quantity <= p.minStockLevel AND p.dataStatus = 'ACTIVE'")
    List<Product> findLowStockProducts();

    @Query("SELECT COUNT(p) FROM Product p")
    long countAllProducts();

    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', ?1, '%')) OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', ?1, '%'))) AND p.dataStatus = 'ACTIVE'")
    List<Product> searchActiveProducts(String query);

}
