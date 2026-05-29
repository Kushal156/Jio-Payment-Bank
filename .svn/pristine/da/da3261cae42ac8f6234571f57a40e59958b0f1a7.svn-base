package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreviewApplicationResponseDTO {

	private String externalAppRefNumber;
	private String applicationNumber;
	private String status;
	private PreviewDataDTO data;
	
	private ErrorDetails error; 
}
