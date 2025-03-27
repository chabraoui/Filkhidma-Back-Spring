package com.sid.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sid.exceptions.CategoryException;
import com.sid.requests.CategoryRequest;
import com.sid.responses.CategoryResponse;
import com.sid.responses.ErrorMessages;
import com.sid.services.CategoryService;
import com.sid.shared.CategoryName;
import com.sid.shared.Utils;
import com.sid.shared.dto.CategoryDto;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

	@Autowired
	CategoryService categoryService;
	@Autowired
	Utils utils;

	@GetMapping
	public List<CategoryResponse> listOfCategories(@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "10") int limit) {

		List<CategoryResponse> categoryResponse = new ArrayList<>();

		List<CategoryDto> categories = categoryService.Categories(page, limit);

		for (CategoryDto categoryDto : categories) {

			ModelMapper modelMapper = new ModelMapper();

			CategoryResponse cat = modelMapper.map(categoryDto, CategoryResponse.class);

			categoryResponse.add(cat);
		}

		return categoryResponse;
	}

	@GetMapping(value = "/{categoryName}")
	public CategoryResponse category(@PathVariable String categoryName) {

		CategoryName cat;
		try {
			cat = CategoryName.valueOf(categoryName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CategoryException(ErrorMessages.CAT_Not_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND);
		}

		CategoryDto catDto = categoryService.getCategory(cat);
		return utils.mapToDto(catDto, CategoryResponse.class);
	}

	@PostMapping
	public CategoryResponse createCategory(@RequestBody CategoryRequest categoryRequest, Principal principal) {
		if (categoryRequest.getCategoryName() == null || categoryRequest.getCategoryName().isEmpty()) {
			throw new CategoryException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage(),HttpStatus.BAD_REQUEST);
		}
		CategoryName cat;
		try {
			cat = CategoryName.valueOf(categoryRequest.getCategoryName().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CategoryException(ErrorMessages.CAT_Not_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND);
		}
		if (categoryService.existeCategory(cat)) {
			throw new CategoryException("La catégorie '" + cat + "' existe déjà.",HttpStatus.NOT_FOUND);
		}
		CategoryDto catDto = utils.mapToDto(categoryRequest, CategoryDto.class);
		CategoryDto addCatDto = categoryService.addCategory(catDto, principal.getName());
		return utils.mapToDto(addCatDto, CategoryResponse.class);
	}

//	@PutMapping(value = "/{categoryName}")
//	public CategoryResponse updateCategory(@RequestBody CategoryRequest categoryRequest,
//			@PathVariable String categoryName, Principal principal) throws JsonProcessingException {
//		CategoryName cat;
//		try {
//			cat = CategoryName.valueOf(categoryName.toUpperCase());
//		} catch (IllegalArgumentException e) {
//			throw new CategoryException(ErrorMessages.CAT_Not_FOUND.getErrorMessage());
//		}
//		CategoryDto catDto = utils.mapToDto(categoryRequest, CategoryDto.class);
//		catDto.setCategoryName(CategoryName.valueOf(categoryRequest.getCategoryName().toUpperCase()));
//		CategoryDto updatedCatDto = categoryService.updateCategory(catDto, cat, principal.getName());
//		return utils.mapToDto(updatedCatDto, CategoryResponse.class);
//
//	}

	@DeleteMapping(value = "/{categoryName}")
	public void deleteCategory(@PathVariable String categoryName, Principal principal) {
		CategoryName cat;
		try {
			cat = CategoryName.valueOf(categoryName.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new CategoryException(ErrorMessages.CAT_Not_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND);
		}
		categoryService.removeCategory(cat, principal.getName());
	}

}
