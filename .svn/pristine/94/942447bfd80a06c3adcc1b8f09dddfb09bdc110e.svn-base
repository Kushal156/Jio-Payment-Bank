package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerEAuthResponseDTO {

	private String externalAppRefNumber;
	private String applicationNumber;
	private String message;
	private String status;
	private DataDTO data;
	
	private ErrorDetails error;
}
