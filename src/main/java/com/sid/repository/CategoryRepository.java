package com.sid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sid.entity.CategoryEntity;
import com.sid.shared.CategoryName;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

	CategoryEntity findByCategoryName(CategoryName categoryName);

}
