package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JpbDmtVerifyOtpRequestDto {

	private String amount;
	private Users user;
	private String scope;
	private List<AuthenticateDTO> authenticateList;
	private SecureDTO secure;
	private String extraInfo;
	private String purpose;
	
}
