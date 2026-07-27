package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PayerDto {

	private Integer type;
	private String userId;
	private Mobile mobile;
	private String bankId;
	private String bankName;
	private AadhaarDTO aadhaar;
	
	//DMT Transaction Params
	private String accountAlias;
	private String bankIfsc;
	private String accountNumber;
	private String name;
	
}
