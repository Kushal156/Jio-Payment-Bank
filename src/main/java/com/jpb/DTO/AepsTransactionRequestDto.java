package com.jpb.DTO;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AepsTransactionRequestDto {

	private TransactionAeps transaction;
	private Amount amount;
	private PayerDto payer;
	private Payee payee;
	private Secure secure;
	private ConsentDTO auth;
}
