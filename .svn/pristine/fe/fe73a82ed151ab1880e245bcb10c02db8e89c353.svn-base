package com.jpb.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jpb.Entity.WalletMasterEntity;

public interface WalletMasterRepository extends JpaRepository<WalletMasterEntity, Long>{

	 List<WalletMasterEntity> findBySubServiceIdAndDcAndTransactionReferenceId(
	            String subServiceId,
	            String dc,
	            String transactionReferenceId);
}
