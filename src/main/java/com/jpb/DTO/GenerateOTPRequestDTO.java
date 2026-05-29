package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenerateOTPRequestDTO {

	private String externalAppRefNumber;
    private String apiVersion;
    private String applicationType;
    private String applicationSubType;
    private String initiatingEntityId;

    private ActionDTO action;
    private OrganizationDTO organization;
    private BCDetailsDTO bcDetails;
    private List<PersonDTO> persons;
}
