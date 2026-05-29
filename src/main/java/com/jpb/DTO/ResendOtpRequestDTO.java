package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResendOtpRequestDTO {

	private String applicationNumber;
	private OrganizationDTO organization;
    private String applicationType;
    private String applicationSubType;
    private String initiatingEntityId;
    private String apiVersion;
    private String app;
    private ActionDTO action;
    private BCDetailsDTO bcDetails;
}
