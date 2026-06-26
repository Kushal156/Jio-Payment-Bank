package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.ErrorCodeEntity;

public interface CustomerErrorMasterRepository extends JpaRepository<ErrorCodeEntity, Integer>{

	Optional<ErrorCodeEntity> findByErrorCode(String errorCode);
}
