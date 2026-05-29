package com.jpb.DTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthTokenRequestDTO {

	private ApplicationDTO application;
    private List<AuthenticateDTO> authenticateList;
    private Integer purpose;
    private String scope;
    private SecureDTO secure;
}
