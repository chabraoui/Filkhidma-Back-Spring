package com.sid.services.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.sid.entity.CategoryEntity;
import com.sid.entity.UserEntity;
import com.sid.exceptions.CategoryException;
import com.sid.exceptions.UserException;
import com.sid.repository.CategoryRepository;
import com.sid.repository.UserRepository;
import com.sid.responses.ErrorMessages;
import com.sid.services.CategoryService;
import com.sid.shared.CategoryName;
import com.sid.shared.RoleName;
import com.sid.shared.Utils;
import com.sid.shared.dto.CategoryDto;

@Service
public class CatgeoryServiceImpl implements CategoryService {

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	Utils utils;

	@Override
	public List<CategoryDto> Categories(int page, int limit) {
		Pageable pageableRequest = utils.createPageable(page, limit);
		Page<CategoryEntity> categories = categoryRepository.findAll(pageableRequest);
		List<CategoryEntity> listOfCat = categories.getContent();
		if (listOfCat.isEmpty())
			throw new CategoryException(ErrorMessages.CAT_EMPTY.getErrorMessage(),HttpStatus.NOT_FOUND);
		return utils.mapToLists(listOfCat, CategoryDto.class);
	}

	@Override
	public CategoryDto addCategory(CategoryDto category, String email) {
		checkAuth(email);
		CategoryEntity catEntity = utils.mapToDto(category, CategoryEntity.class);
		CategoryEntity catSave = categoryRepository.save(catEntity);
		return utils.mapToDto(catSave, CategoryDto.class);
	}

	@Override
	public void removeCategory(CategoryName categoryName, String email) {
		checkAuth(email);
		CategoryEntity cat = categoryRepository.findByCategoryName(categoryName);
		Optional.ofNullable(cat)
				.orElseThrow(() -> new CategoryException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND));
		categoryRepository.delete(cat);
	}

//	@Override
//	public CategoryDto updateCategory(CategoryDto category, CategoryName categoryName, String email) {
//		checkAuth(email);
//		CategoryEntity cat = categoryRepository.findByCategoryName(categoryName);
//		Optional.ofNullable(cat)
//				.orElseThrow(() -> new CategoryException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage()));
//		cat.setCategoryName(category.getCategoryName());
//		CategoryEntity catUpdated = categoryRepository.save(cat);
//		return utils.mapToDto(catUpdated, CategoryDto.class);
//	}

	@Override
	public CategoryDto getCategory(CategoryName categoryName) {
		CategoryEntity cat = categoryRepository.findByCategoryName(categoryName);
		Optional.ofNullable(cat)
				.orElseThrow(() -> new CategoryException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND));
		return utils.mapToDto(cat, CategoryDto.class);
	}

	private void checkAuth(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		Optional.ofNullable(userEntity)
				.orElseThrow(() -> new UserException(ErrorMessages.NO_AUTH_FOUND.getErrorMessage(),HttpStatus.NOT_FOUND));
		boolean isAdminOrSuperAdmin = userEntity.getRole().stream()
				.anyMatch(role -> role.getRoleName().equals(RoleName.SUPER_ADMIN));
		if (!isAdminOrSuperAdmin) {
			throw new UserException(ErrorMessages.NO_AUTORIZ_REQUEST.getErrorMessage(),HttpStatus.FORBIDDEN);
		}
	}

	@Override
	public boolean existeCategory(CategoryName categoryName) {
		CategoryEntity cat = categoryRepository.findByCategoryName(categoryName);
		return cat != null;
	}

}
