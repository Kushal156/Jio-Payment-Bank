package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticateDTO {

	private Integer mode;
    private String value;
    
    //DMT generate OTP params
    private String action;
    
    //DMT Params
    private AadhaarDTO aadhaar;
    private String consent;
  	private String consentCode;
}
