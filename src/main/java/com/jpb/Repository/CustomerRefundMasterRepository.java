package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.CustomerRefundMasterEntity;

public interface CustomerRefundMasterRepository extends JpaRepository<CustomerRefundMasterEntity, Integer>{

	Optional<CustomerRefundMasterEntity> findByExternalAppRefNumber(String externalAppRefNumber);

}
