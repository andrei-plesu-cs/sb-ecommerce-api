package com.andrei.plesoianu.sbecom.service;

import com.andrei.plesoianu.sbecom.config.SortOrder;
import com.andrei.plesoianu.sbecom.exceptions.ApiException;
import com.andrei.plesoianu.sbecom.exceptions.NotFoundException;
import com.andrei.plesoianu.sbecom.model.Category;
import com.andrei.plesoianu.sbecom.payload.CategoryDto;
import com.andrei.plesoianu.sbecom.payload.CategoryResponse;
import com.andrei.plesoianu.sbecom.repositories.CategoryRepository;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    public CategoryServiceImpl(@NonNull CategoryRepository categoryRepository,
                               @NonNull ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, SortOrder sortOrder) {
        Sort sortByAndOrder = switch (sortOrder) {
            case ASCENDING -> Sort.by(sortBy).ascending();
            case DESCENDING -> Sort.by(sortBy).descending();
        };

        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);

        var response = new CategoryResponse();
        response.setContent(categoryPage.stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .toList());
        response.setPageNumber(categoryPage.getNumber());
        response.setPageSize(categoryPage.getSize());
        response.setTotalElements(categoryPage.getTotalElements());
        response.setTotalPages(categoryPage.getTotalPages());
        response.setLast(categoryPage.isLast());
        return response;
    }

    @Override
    public CategoryDto createCategory(CategoryDto categoryDto) {
        if (categoryRepository.existsByCategoryName(categoryDto.getCategoryName())) {
            throw new ApiException("A category with name %s already exists".formatted(categoryDto.getCategoryName()));
        }
        var categoryToSave = new Category();
        categoryToSave.setCategoryName(categoryDto.getCategoryName());
        categoryRepository.save(categoryToSave);
        return modelMapper.map(categoryToSave, CategoryDto.class);
    }

    @Override
    public CategoryDto deleteCategory(Long categoryId) {
        Category dbCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        categoryRepository.delete(dbCategory);
        return modelMapper.map(dbCategory, CategoryDto.class);
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category dbCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(Category.class, categoryId));

        dbCategory.setCategoryName(categoryDto.getCategoryName());
        categoryRepository.save(dbCategory);
        return modelMapper.map(dbCategory, CategoryDto.class);
    }
}
