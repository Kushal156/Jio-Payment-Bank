package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinancialDetailsDTO {

	private String panNumber;
	
	private String nameAsPerPan;
	private String dobAsPerPan;
	
	//submit app params
	private String sourceOfIncome;
	//private Integer annualSalary;
	private String annualSalary;
	private String occupation;
}
