package com.jpb.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.CustomerMasterEntity;

public interface CustomerMasterRepository extends JpaRepository<CustomerMasterEntity, Integer>{

	Optional<CustomerMasterEntity> findByApplicationNumberAndExternalAppRefNumber(
	        String applicationNumber,
	        String externalAppRefNumber
	);

	Optional<CustomerMasterEntity> findByApplicationNumber(String applicationNumber);
	
	Optional<CustomerMasterEntity> findByExternalAppRefNumber(String externalAppRefNumber);
	
	List<CustomerMasterEntity> findByMobileNo(String mobileNo);
	
	List<CustomerMasterEntity> findByNextActionTypeAndNextActionSubType(String type, String subType);
}
