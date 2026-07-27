package com.jpb.DTO;

import java.util.List;


import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JpbDmtGenerateOtpRequestDto {
	
	private Users user;
	private String scope;
	private List<AuthenticateDTO> authenticateList;
	private String action;
	private String extraInfo;
	private String purpose;
	private SecureDTO secure;
	private PayerDto payer; 
	private String amount;
}