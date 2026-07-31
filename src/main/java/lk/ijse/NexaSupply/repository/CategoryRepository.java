package lk.ijse.NexaSupply.repository;

import lk.ijse.NexaSupply.entity.Category;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.dataStatus = 'ACTIVE'")
    List<Category> findAllActiveCategories();

    @Query("SELECT c FROM Category c WHERE c.categoryCode = ?1 AND c.dataStatus = 'ACTIVE'")
    Optional<Category> findActiveByCategoryCode(String categoryCode);

    boolean existsByNameAndDataStatus(String name, DataStatus dataStatus);

    @Query("SELECT COUNT(c) FROM Category c")
    long countAllCategories();

}
