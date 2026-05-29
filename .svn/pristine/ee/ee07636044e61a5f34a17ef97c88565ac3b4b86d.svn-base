package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerPanAadharVerifyResponseDTO {

	private String externalAppRefNumber;
	private String applicationNumber;
	private String status;
	private String message;
	private NextActionDTO nextAction;
	private PanAadharVerifyDataDTO data;
	
	private ErrorDetails error;
	
}
