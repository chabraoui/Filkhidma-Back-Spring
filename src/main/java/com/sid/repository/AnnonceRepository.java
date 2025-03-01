package com.sid.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.sid.entity.UserEntity;
import com.sid.entity.VilleEntity;
import com.sid.shared.CategoryName;
import com.sid.shared.VilleName;
import com.sid.entity.AnnonceEntity;
import com.sid.entity.CategoryEntity;

@Repository
public interface AnnonceRepository extends JpaRepository<AnnonceEntity, Long>, JpaSpecificationExecutor<AnnonceEntity>{

	AnnonceEntity findByAnnonceId(String annonceId);

	Page<AnnonceEntity> findByUser(UserEntity currentUser, Pageable pageable);

	Page<AnnonceEntity> findByCategory(CategoryEntity category, Pageable page);

	Page<AnnonceEntity> findByVille(VilleEntity ville, Pageable page);

	//@Query(value = "SELECT * FROM annonces a WHERE a.category LIKE :category AND a.ville LIKE :ville", nativeQuery = true)
	//Page<AnnonceEntity> findByCategoryAndVille(@Param("category") String category, @Param("ville") String ville,
		//	Pageable page);

	//@Query(value = "SELECT a FROM AnnonceEntity a " + "WHERE (:category IS NULL OR a.category.id = :category.id) "
//			+ "AND (:keyword IS NULL OR (a.name LIKE %:keyword% OR a.description LIKE %:keyword%)) "
//			+ "AND (:ville IS NULL OR a.ville.id = :ville.id)")
	//Page<AnnonceEntity> search(@Param("category") CategoryEntity category, @Param("ville") VilleEntity ville,
		//	@Param("keyword") String keyword, Pageable pageable);

//	@Query(value = "SELECT * FROM annonces a " + "WHERE (:category IS NULL OR a.category LIKE %:category%) "
//			+ "AND (:keyword IS NULL OR (a.name LIKE %:keyword% OR a.description LIKE %:keyword%))"
//			+ "AND (:ville IS NULL OR a.ville LIKE %:ville%)", nativeQuery = true)
//	Page<AnnonceEntity> search(@Param("category") CategoryName category, @Param("ville") VilleName ville,
//			@Param("keyword") String keyword, Pageable pageable);
}

// List<AnnonceEntity> findByCategory(String category);

// @Query(value = "SELECT * FROM annonces a where a.category LIKE %:x% ",
// nativeQuery = true)
// Page<AnnonceEntity> findByCategory(@Param("x") String category, Pageable
// page);

// @Query(value="SELECT * FROM annonces a WHERE a.category LIKE :x",
// nativeQuery=true)
// Page<AnnonceEntity> findByCategory(@Param("x") String category, Pageable
// page);

// @Query(value = "SELECT * FROM annonces a where a.ville LIKE %:x% ",
// nativeQuery = true)
// Page<AnnonceEntity> findByVille(@Param("x") String ville, Pageable page);

//	
//	@Query(value="SELECT DISTINCT  category FROM annonces ", nativeQuery=true)
//	List<String> allCategory();
//	
//	@Query(value="SELECT DISTINCT  ville FROM annonces ", nativeQuery=true)
//	List<String> allVilles();

// Recherche par catégorie et ville
