package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreviewDataDTO {

	private String applicationNumber;
    private String parentApplicationNumber;
    private String externalAppRefNumber;
    private String applicationType;
    private String applicationSubType;
    private String initiatingEntityId;

    private List<PersonDTO> persons;
    private List<ProductDTO> products;
    private OrganizationDTO organization;
    private BCDetailsDTO bcDetails;
    private List<DBTRecordDTO> dbtRecords;
}
