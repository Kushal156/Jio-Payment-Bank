package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerEAuthRequestDTO {

	private String applicationNumber;
	private String externalAppRefNumber;
	private String apiVersion;

	private ActionDTO action;

	private String applicationType;
	private String applicationSubType;
	private String initiatingEntityId;

	private OrganizationDTO organization;

	private List<AuthenticateEkycDTO> authenticateList;
	private List<ConsentDTO> consents;

	private BCDetailsDTO bcDetails;
}
