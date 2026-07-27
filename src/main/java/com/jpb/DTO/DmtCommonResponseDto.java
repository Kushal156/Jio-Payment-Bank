package com.jpb.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class DmtCommonResponseDto {
		
	private String status; 
	private DataDTO data;
	private ErrorDetails error;
}
