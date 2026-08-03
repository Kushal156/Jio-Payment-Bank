package com.jpb.DTO;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AepsTransaction2FaDto {

	private TransactionAeps transaction;

	private PayerDto payee;

	private PayerDto payer;

	private SecureDTO secure;

	private Users user;

	private String scope;

	private String purpose;

	private BigDecimal amount;

	private String extraInfo;

	private List<AuthenticateDTO> authenticateList;
}
