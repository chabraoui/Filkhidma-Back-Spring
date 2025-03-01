package com.sid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sid.entity.VilleEntity;
import com.sid.shared.VilleName;
@Repository
public interface VilleRepository extends JpaRepository<VilleEntity, Long> {
	
	 VilleEntity findByVilleName(VilleName villeName);

}
