package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.DMTTransactionMasterEntity;

public interface DMTTransactionMasterRepository extends JpaRepository<DMTTransactionMasterEntity, Long>{

	Optional<DMTTransactionMasterEntity> findByClientReferenceId(String clientRefID);

}
