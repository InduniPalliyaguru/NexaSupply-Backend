package lk.ijse.NexaSupply.service.impl;

import lk.ijse.NexaSupply.dto.product.CategoryDTO;
import lk.ijse.NexaSupply.entity.Category;
import lk.ijse.NexaSupply.enumeration.DataStatus;
import lk.ijse.NexaSupply.exception.CustomException;
import lk.ijse.NexaSupply.repository.CategoryRepository;
import lk.ijse.NexaSupply.service.AuditLogService;
import lk.ijse.NexaSupply.service.CategoryService;
import lk.ijse.NexaSupply.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final AuditLogService auditLogService;

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        log.info("Execute createCategory Method");

        if (categoryRepository.existsByNameAndDataStatus(categoryDTO.getCategoryName(), DataStatus.ACTIVE)) {
            log.error("Category already exists");
            throw new CustomException(400, "Category with name '" + categoryDTO.getCategoryName() + "' already exists!");
        }

        long count = categoryRepository.countAllCategories() + 1;
        String catCode = String.format("CAT-%04d", count);

        Category category = new Category();
        category.setCategoryCode(catCode);
        category.setName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());
        category.setDataStatus(DataStatus.ACTIVE);

        Category saved = categoryRepository.save(category);

        String action = "CREATED_CATEGORY | Code: " + saved.getCategoryCode() + " | Name: " + saved.getName();
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);

        return mapToDTO(saved);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        log.info("Execute updateCategory Method");

        Optional<Category> optional = categoryRepository.findActiveByCategoryCode(categoryDTO.getCategoryCode());
        if (optional.isEmpty()) {
            log.error("Category not found");
            throw new CustomException(400, "Category with code '" + categoryDTO.getCategoryCode() + "' not found!");
        }

        Category category = optional.get();
        String oldName = category.getName();

        category.setName(categoryDTO.getCategoryName());
        category.setDescription(categoryDTO.getDescription());

        Category updated = categoryRepository.save(category);

        String action = "UPDATED_CATEGORY | Code: " + updated.getCategoryCode() + " | Old Name: " + oldName + " -> New Name: " + updated.getName();
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);

        return mapToDTO(updated);
    }

    @Override
    public CategoryDTO getCategoryByCode(String categoryCode) {
        log.info("Execute getCategoryByCode Method");

        Optional<Category> optional = categoryRepository.findActiveByCategoryCode(categoryCode);
        if (optional.isEmpty()) {
            log.error("Category not found with code '{}'", categoryCode);
            throw new CustomException(400, "Category with code '" + categoryCode + "' not found!");
        }

        Category category = optional.get();
        return mapToDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllActiveCategories() {
        log.info("Execute getAllActiveCategories Method");

        List<Category> categories = categoryRepository.findAllActiveCategories();

        List<CategoryDTO> categoryDTOList = new ArrayList<>();
        for (Category category : categories) {
            CategoryDTO dto = mapToDTO(category);
            categoryDTOList.add(dto);
        }
        return categoryDTOList;
    }

    @Override
    public void deleteCategory(String categoryCode) {
        log.info("Execute deleteCategory Method");

        Optional<Category> optional = categoryRepository.findActiveByCategoryCode(categoryCode);
        if (optional.isEmpty()) {
            log.error("Category not found with code :'{}'", categoryCode);
            throw new CustomException(400, "Category with code '" + categoryCode + "' not found!");
        }

        Category category = optional.get();
        category.setDataStatus(DataStatus.INACTIVE);
        categoryRepository.save(category);

        String action = "DELETED_CATEGORY | Code: " + category.getCategoryCode() + " | Name: " + category.getName();
        auditLogService.logAction(SecurityUtils.getCurrentUserEmail(), action);
    }

    private CategoryDTO mapToDTO(Category category) {
        return new CategoryDTO(
                category.getCategoryId(),
                category.getCategoryCode(),
                category.getName(),
                category.getDescription()
        );
    }

}
