package com.sid.repository;


import java.awt.print.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sid.entity.RoleEntity;
import com.sid.entity.UserEntity;
import com.sid.shared.RoleName;
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity,Long>{
	
//	@Query("SELECT u FROM UserEntity u JOIN u.role r WHERE r.roleName = :roleName")
//    Page<UserEntity> findByRoleName(@Param("roleName") RoleName roleName, Pageable pageable);
	
	   RoleEntity  findByRoleName(RoleName roleName);



}
