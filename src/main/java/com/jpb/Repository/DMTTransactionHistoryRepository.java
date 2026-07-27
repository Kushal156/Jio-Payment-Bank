package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.DMTTransactionHistoryEntity;

public interface DMTTransactionHistoryRepository extends JpaRepository<DMTTransactionHistoryEntity, Long>{

}
