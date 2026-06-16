package com.jpb.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jpb.Entity.CustomerMasterEntity;

public interface CustomerMasterRepository extends JpaRepository<CustomerMasterEntity, Integer>{

	Optional<CustomerMasterEntity> findByApplicationNumberAndExternalAppRefNumber(
	        String applicationNumber,
	        String externalAppRefNumber
	);

	Optional<CustomerMasterEntity> findByApplicationNumber(String applicationNumber);
	
	Optional<CustomerMasterEntity> findByExternalAppRefNumber(String externalAppRefNumber);
	
	List<CustomerMasterEntity> findByMobileNo(String mobileNo);
	
//	List<CustomerMasterEntity> findByNextActionTypeAndNextActionSubType(String type, String subType);
	
	@Query("""
		    SELECT c
		    FROM CustomerMasterEntity c
		    WHERE c.nextActionType = :nextActionType
		      AND c.nextActionSubType = :nextActionSubType
		      AND (c.jioStatus IN :jioStatuses OR c.jioStatus IS NULL)
		  """)
		List<CustomerMasterEntity> applicationStatusRecords(
		        @Param("nextActionType") String nextActionType,
		        @Param("nextActionSubType") String nextActionSubType,
		        @Param("jioStatuses") List<String> jioStatuses);
	
	@Query(""" 
			SELECT c from CustomerMasterEntity c
			where c.mobileNo = :mobileNo
			AND c.jioStatus IN :jioStatus AND c.jioStage in :jioStage
			""")
	Optional<CustomerMasterEntity> findFailedCustomerRefNo(@Param("mobileNo") String mobileNo, 
			@Param("jioStatus") List<String> jioStatus, @Param("jioStage") List<String> jioStage);
}
