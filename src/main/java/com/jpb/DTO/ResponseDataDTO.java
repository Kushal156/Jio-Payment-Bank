package com.jpb.DTO;

import java.time.ZonedDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDataDTO {

	private UidaiDataDTO uidaiData;
	
	@JsonFormat(pattern = "EEE MMM dd HH:mm:ss z yyyy", locale = "en")
	private ZonedDateTime transactionTime;
	private String remitterMobile;
	private String beneficiaryName;
	private String beneficiaryAccountNumber;
	private String beneficiaryBankName;
	private List<TransactionDMT> transactions;
	
	private String rrn;
	private String transactionId;
	private String originalId;
	private Double transactionNetAmount;
	private Double transactionGrossAmount;
	
	private TransactionAeps transaction;
}
