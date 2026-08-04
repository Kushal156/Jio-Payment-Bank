package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.jpb.Entity.DMTAddBeneficiaryEntity;

import jakarta.transaction.Transactional;

public interface DMTAddBeneficiaryRepository extends JpaRepository<DMTAddBeneficiaryEntity, Long>{

	Optional<DMTAddBeneficiaryEntity> findByDmtCustomerIdAndDmtPartnerIdAndAccountNo(Integer customerID,
			Integer partnerId, String accNo);

	Optional<DMTAddBeneficiaryEntity> findByDmtRecipientId(String beneficiaryId);
}
