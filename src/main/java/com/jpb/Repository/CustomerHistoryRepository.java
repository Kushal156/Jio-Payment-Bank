package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.CustomerHistoryEntity;

public interface CustomerHistoryRepository extends JpaRepository<CustomerHistoryEntity, Integer>{

}
