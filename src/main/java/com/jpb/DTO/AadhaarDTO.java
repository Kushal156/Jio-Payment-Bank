package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AadhaarDTO {

	private String type; // UID
	private String value;
	
	//Pan-Aadhar Verify Params
    private String maskedAadhaar;
    private String name;
    private String dob;
    private AddressDTO address;
    private String photo;
    private String gender;

    private String timestamp;
    private String authCode;
    private String authMode;
    private String saTxnId;
    private String aadhaarXml;
    private Boolean isResidentForeigner;
    private String demoAuthStatus;
}
