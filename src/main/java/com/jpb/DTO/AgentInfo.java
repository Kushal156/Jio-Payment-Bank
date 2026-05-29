package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentInfo {

	private String name;
    private String id;
    private String city;
    private String stateCode;
    private String pinCode;
    private Double latitude;
    private Double longitude;
    private String storeCode;
    private String parentAgentId;

    private OrganizationDTO organization;

    private String authorization;
    private String address;
}
