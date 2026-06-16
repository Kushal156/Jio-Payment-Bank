package com.jpb.Repository;

import java.util.LinkedList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import com.jpb.Entity.CustomerListEntity;

public interface CustomerListRepository extends JpaRepository<CustomerListEntity, String>{

	@Procedure(procedureName = "BankingJio.usp_GetCustomerApplicationStatus")
	LinkedList<CustomerListEntity> customerList(@Param("vkid") String vkid);
	
}
