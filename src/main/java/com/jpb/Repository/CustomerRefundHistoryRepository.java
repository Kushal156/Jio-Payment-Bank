package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.CustomerRefundHistoryEntity;

public interface CustomerRefundHistoryRepository extends JpaRepository<CustomerRefundHistoryEntity, Integer>{

}
