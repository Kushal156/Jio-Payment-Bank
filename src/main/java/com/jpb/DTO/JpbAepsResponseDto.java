package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JpbAepsResponseDto {

	private String responseCode;

	private String responseMessage;

	@JsonProperty("responseData")
	private ResponseData responsedata;

	private String status;

	private String otpReferenceId;

	private AadhaarDTO aadhaar;
	

}