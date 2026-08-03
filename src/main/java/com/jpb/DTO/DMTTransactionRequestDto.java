package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMTTransactionRequestDto {

	private TransactionAeps transaction;
	private Amount amount;
	private PayerDto payer;
	private PayerDto payee;
	private SecureDTO secure;
	private ConsentDTO auth;

	// DMT Params
	private Users user;
	private String scope;
	private String purpose;
	private String amounts;
	private String extraInfo;
	private List<AuthenticateDTO> authenticateList;
}
