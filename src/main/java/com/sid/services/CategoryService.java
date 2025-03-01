package com.sid.services;

import java.util.List;

import com.sid.shared.CategoryName;
import com.sid.shared.dto.CategoryDto;

public interface CategoryService {

	List<CategoryDto> Categories(int page, int limit);

	CategoryDto addCategory(CategoryDto category, String email);

	void removeCategory(CategoryName categoryName, String email);

	//CategoryDto updateCategory(CategoryDto category, CategoryName categoryName, String email);

	CategoryDto getCategory(CategoryName categoryName);
	
	boolean existeCategory(CategoryName categoryName);

}
