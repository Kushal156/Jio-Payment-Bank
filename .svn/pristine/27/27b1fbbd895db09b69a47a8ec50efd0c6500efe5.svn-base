package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.jpb.Entity.CreditTransactionEntity;

import jakarta.transaction.Transactional;

public interface CreditTransactionRepository extends JpaRepository<CreditTransactionEntity, String> {

	@Transactional
    @Procedure(procedureName = "VLPaymentGateway.CreditTransaction")
	CreditTransactionEntity creditTransaction(

            @Param("transactionRefID") String transactionRefID,
            @Param("remarks") String remarks,
            @Param("ServiceId") String serviceId,
            @Param("subServiceId") String subServiceId,
            @Param("subSubServiceId") String subSubServiceId,
            @Param("vkid") String vkid,
            @Param("walletId") String walletId,
            @Param("transactionAmount") Double transactionAmount
    );
}
