package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationStatusResponseDTO {

	private String externalAppRefNumber;
	private String applicationNumber;
	private String status;
	private ApplicationSubmitData data;
	
	private ErrorDetails error;
}
