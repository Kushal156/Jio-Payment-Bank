package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.DMTTransactionDetailsEntity;

public interface DMTTransactionDeatilRepository extends JpaRepository<DMTTransactionDetailsEntity, Long> {

}
