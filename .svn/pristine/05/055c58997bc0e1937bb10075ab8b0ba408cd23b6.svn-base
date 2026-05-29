package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PanUpdateResponseDTO {

	private String applicationNumber;
	private String status;
	private String message;

	private PanDataDTO data;
	private ErrorDetails error;
}
