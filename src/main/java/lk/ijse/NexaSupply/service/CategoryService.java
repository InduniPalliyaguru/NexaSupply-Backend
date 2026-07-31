package lk.ijse.NexaSupply.service;

import lk.ijse.NexaSupply.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO updateCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryByCode(String categoryCode);

    List<CategoryDTO> getAllActiveCategories();

    void deleteCategory(String categoryCode);

}
