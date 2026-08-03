package com.jpb.DTO;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AepsCommonRequestDto {

	private String agentId;
	
	private String transactionTime;
	
	private String pincode; 
	
	private String stateid;
	
	private String netAmount;
	
	private String grossAmount;
	
	private String number;
	
	private String bankId;
	
	private String AadharNo;
	
	private String description;
	
	private String fingerprint;
	
	private String osType;
	
	private String osVer;
	
	private String model;
	
	private String latitude;
	
	private String longitude;
	
	private String entityId;
	
	private Date startDate;
	
	private Date endDate;
	
	private String orgname;
	
	private String entityType;
	
	private String userId;
	
	private String scope;
	
	private List<AuthenticateDTO> authenticateList;
	
	private String purpose;
	
	private String amount;
	
	private String extraInfo;
	
	private String xmlBiometricString;
	private String transactionAmount;
	private String adhaarNumber;
	private String authenticationToken;
	private String mobileNumber;
	private String merchantTranId;
	private String nationalBankIdentificationNumber;
	
}
