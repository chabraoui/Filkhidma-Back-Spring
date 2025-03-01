package com.sid.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sid.entity.RoleEntity;
import com.sid.entity.UserEntity;
import com.sid.shared.RoleName;
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
	UserEntity findByEmail(String email);
	UserEntity findByUserId(String userId);
	Page<UserEntity> findByRole(RoleEntity role, Pageable page);
//    @Query("SELECT u FROM UserEntity u JOIN u.role r WHERE r.roleName = :roleName")
//    Page<UserEntity> findByRole(@Param("roleName") RoleName roleName, Pageable pageable);
}
