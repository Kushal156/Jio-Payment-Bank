package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.NSDLPanVerificationEntity;

public interface NSDLPanVerificationRepository extends JpaRepository<NSDLPanVerificationEntity, Integer>{

	NSDLPanVerificationEntity findByPanNumber(String pan);

	
}
