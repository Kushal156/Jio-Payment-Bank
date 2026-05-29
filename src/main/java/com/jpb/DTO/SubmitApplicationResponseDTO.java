package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmitApplicationResponseDTO {

	private String externalAppRefNumber;
	private String applicationNumber;
	private String status;
	//private List<String> data;
	private Object data;
	
	private ErrorDetails error;
}
