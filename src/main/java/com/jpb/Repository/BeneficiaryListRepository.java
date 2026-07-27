package com.jpb.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.jpb.Entity.BeneficiaryListEntity;

import jakarta.transaction.Transactional;

public interface BeneficiaryListRepository extends JpaRepository<BeneficiaryListEntity, Long>{

	@Transactional
    @Procedure(procedureName = "DMT.usp_crud_dmt_bene_details")
	List<BeneficiaryListEntity> beneficiaryList(
            @Param("mobileNo") String mobileNo
    );
}
