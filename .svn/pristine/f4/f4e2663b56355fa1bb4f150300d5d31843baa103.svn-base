package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmitApplicationRequstDTO {

	private String applicationNumber;
	private String externalAppRefNumber;
	private String apiVersion;
	private ActionDTO action;
	private String applicationType;
	private String applicationSubType;
	private String initiatingEntityId;
	private OrganizationDTO organization;
	private BCDetailsDTO bcDetails;
	
	
	private List<PersonDTO> persons;
	private List<AuthenticateEkycDTO> authenticateList;
	private List<ConsentDTO> consents;
	private List<ProductDTO> products;
}
