package com.jpb.DTO;

import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SaveAgentRequestDTO {

	private String externalAppRefNumber;
    private String apiVersion;
    private String applicationType;
    private String applicationSubType;
    private String initiatingEntityId;
    private String app;

    private ActionDTO action;
    private OrganizationDTO organization;

    private List<PersonDTO> persons;
    private List<ProductDTO> products;
}
