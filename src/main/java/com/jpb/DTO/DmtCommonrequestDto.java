package com.jpb.DTO;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DmtCommonrequestDto {

	private String mobile;
	private String emailAddress;
	private String discription;
	private String agentId;
	private String stateid;
	private String pincode;
	private String netAmount;
	private String grossAmount;
    private String Aadhaarnumber; 
	private String fingerprint;
	private String osType;
	private String osVer;
	private String model;
	private String latitude;
	private String longitude;
	private String entityId;
	
	private String OTP;
	private String authorizationCode;
	private String aadharToken;
	private String firstName;
	private String lastName;
	private String middleName;
	private Integer occupationCode;
	private String district;
	private String houseNumber;
	private String landmark;
	private String locality;
	private String city;
	private String state;
	private String remitterId;
	private String bankId;
	private String bankIFSC;
	private String accNo;
	private String vkid;
	private String beneAcctVerifyFlag;
	private String bankName;
	private String senderMobileNo;
	private String receiverName;
	private String receiverMobileNo;
	private String serviceName;
	private String beneficiaryId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date dob;
}
