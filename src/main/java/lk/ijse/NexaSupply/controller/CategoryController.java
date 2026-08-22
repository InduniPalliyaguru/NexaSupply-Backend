package lk.ijse.NexaSupply.controller;

import jakarta.validation.Valid;
import lk.ijse.NexaSupply.constant.CommonResponse;
import lk.ijse.NexaSupply.dto.product.CategoryDTO;
import lk.ijse.NexaSupply.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO category = categoryService.createCategory(categoryDTO);
        return new CommonResponse(201, category, "Category created successfully");
    }

    @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO category = categoryService.updateCategory(categoryDTO);
        return new CommonResponse(200, category, "Category updated successfully");
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllCategories() {
        List<CategoryDTO> allActiveCategories = categoryService.getAllActiveCategories();
        return new CommonResponse(200, allActiveCategories, "Categories retrieved successfully");
    }

    @GetMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getCategoryByCode(@PathVariable String code) {
        CategoryDTO categoryByCode = categoryService.getCategoryByCode(code);
        return new CommonResponse(200, categoryByCode, "Category retrieved successfully");
    }

    @DeleteMapping(value = "/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CommonResponse deleteCategory(@PathVariable String code) {
        categoryService.deleteCategory(code);
        return new CommonResponse(200, "Category deleted successfully", "Success");
    }
}
