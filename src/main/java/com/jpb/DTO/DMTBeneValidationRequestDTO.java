package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMTBeneValidationRequestDTO {

	private TransactionAeps transaction;
	private Amount amount;
	private PayerDto payer;
	private PayerDto payee;
	private SecureDTO secure;
}
