package com.jpb.DTO;

import java.time.Instant;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConsentDTO {

	private String consent; 
	private String code; 
	private String version; 
	private String method;
	
	//DMT-EKYC Params
	private String id;
	private String description;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "IST")
	private Instant timeStamp;
	
	//DMT Transaction Params
	private String consentCode;
}
