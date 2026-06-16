package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.ResendVocuherMasterEntity;

public interface ResendVoucherMasterRepository extends JpaRepository<ResendVocuherMasterEntity, Integer> {

	Optional<ResendVocuherMasterEntity> findByMobileNo(String mobileNo);
}
