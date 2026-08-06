package com.jpb.DTO;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDTO {

	private String applicationNumber;
	private List<AuthenticateEkycDTO> authenticateList;
	
	//DMT Check Mobile-No Params
	private String remittanceUserId;
	private String aadharToken;
	private String firstName;
	private String lastName;
	
	//DMT Register User Params
	private String middleName;


	//Aeps common response
	private String terminalId;
	private String requestTransactionTime;
	private String transactionAmount;
	private String transactionStatus;
	private String balanceAmount;
	private String miniStatementBalance;
	private String bankRRN;
	private String transactionType;
	private String jioTransactionId;
	private String merchantTxnId;
	private String errorCode ;
	private String errorMessage;
	private String merchantTransactionId;
	private String bankAccountNumber;
	private String ifscCode;
	private String bcName ;
	private String transactionTime;
	private String agentId;
	private String issuerBank;
	private String customerAadhaarNumber;
	private String customerName;
	private String stan;
	private String uidaiAuthCode;
	private String bcLocation;
	private String demandSheetId;
	private String mobileNumber;
	private String urnId;
	//private String miniStatementStructureModel;
	private String miniOffusStatementStructureModel;
	private String miniOffusFlag;
	private String transactionRemark;
	private String bankName;
	private String prospectNumber;
	private String internalReferenceNumber;
	private String biTxnType;
	private String subVillageName;
	private String virtualId;
	private String userProfileResponseModel;
	private String hindiErrorMessage;
	private String loanAccNo;
	private String responseCode;
	private String jiokAgentId;
	private String additionalData;

	private List<MiniStatementDto> miniStatementStructureModel;

}
