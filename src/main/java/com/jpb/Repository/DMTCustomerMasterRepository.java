package com.jpb.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jpb.Entity.DMTCustomerMasterEntity;

public interface DMTCustomerMasterRepository extends JpaRepository<DMTCustomerMasterEntity, Integer>{

	Optional<DMTCustomerMasterEntity> findByCustomerMobileNoAndDmtPartnerId(String mobile, Integer string);

}
