package com.jpb.DTO;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VerifyOtpRequestDTO {

	private String applicationNumber;
	private String externalAppRefNumber;
	private String apiVersion;
	private ActionDTO action;
	private String applicationType;
	private String applicationSubType;
    private String initiatingEntityId;
    private OrganizationDTO organization;
    private List<ValidationMethodsDTO> validationMethods;
    private BCDetailsDTO bcDetails;
    private List<PersonDTO> persons;
    private SecureDTO secure;
    
}
