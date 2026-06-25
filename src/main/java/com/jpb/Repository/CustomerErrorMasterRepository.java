package com.jpb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.ErrorCodeEntity;

public interface CustomerErrorMasterRepository extends JpaRepository<ErrorCodeEntity, Integer>{

}
