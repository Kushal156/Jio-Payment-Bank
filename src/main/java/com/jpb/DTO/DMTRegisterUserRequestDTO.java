package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMTRegisterUserRequestDTO {

	private String authorizationCode;
	private String aadharToken;
	private Users user;
	
}
