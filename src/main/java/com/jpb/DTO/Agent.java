package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Agent {

	private String id;
	private String subId;
	private AddressDTO address;
	private AgentUser agentUser;
	private OrganizationDTO organization;
	
}


