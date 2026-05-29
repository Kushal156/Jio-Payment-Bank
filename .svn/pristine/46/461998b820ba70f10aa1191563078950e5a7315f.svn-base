package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jpb.Entity.PreOnboardingCheckStatusEntity;

public interface PreOnboardingStatusRespository extends JpaRepository<PreOnboardingCheckStatusEntity, Integer>{
	
	
	Optional<PreOnboardingCheckStatusEntity> findByPreOnboardingCheckIdAndImagesMasterImageid(Integer preOnboardingId,
	        String imageid
	);

}
