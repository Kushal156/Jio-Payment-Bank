package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmailOTPRequestDTO {

	private String applicationNumber;
	private String externalAppRefNumber;
	private OrganizationDTO organization;
    private String applicationType;
    private String applicationSubType;
    private String initiatingEntityId;
    private String apiVersion;
    private String app;
    private ActionDTO action;
    private BCDetailsDTO bcDetails;
    private List<PersonDTO> persons;
    
    private List<ValidationMethodsDTO> validationMethods;
    private SecureDTO secure;
}
