package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentStatusResponseDTO {

	private String applicationNumber;
	private String status;
	private String stage;
	private String message;

	private StatusDataDTO data;
	private ErrorDetails error;
}
