package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.jpb.Entity.DebitTransactionEntity;

import jakarta.transaction.Transactional;

public interface DebitTransactionRepository extends JpaRepository<DebitTransactionEntity, String>{
	
	@Transactional
    @Procedure(procedureName = "VLPaymentGateway.DebitTransaction")
    DebitTransactionEntity debitTransaction(

            @Param("transactionRefID") String transactionRefID,
            @Param("remarks") String remarks,
            @Param("ServiceId") String serviceId,
            @Param("subServiceId") String subServiceId,
            @Param("subSubServiceId") String subSubServiceId,
            @Param("subSubSubServiceId") String subSubSubServiceId,
            @Param("vkid") String vkid,
            @Param("walletId") String walletId,
            @Param("transactionAmount") Double transactionAmount,
            @Param("bcid") String bcid,
            @Param("rrnnumber") String rrnnumber,
            @Param("transactionauthenticationtype") String transactionauthenticationtype,
            @Param("transactiontype") String transactiontype
    );

}
