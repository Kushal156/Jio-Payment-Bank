package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.OnboardingCheckEntity;

public interface PreOnboardingCheckRepository extends JpaRepository<OnboardingCheckEntity, Integer> {
		
   Optional<OnboardingCheckEntity> findByVKID(int vkidnumber);
   
   Optional<OnboardingCheckEntity> findByStatus(String vkid);

}
