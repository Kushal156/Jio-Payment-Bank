package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties
public class SaveAgentResponseDTO {

	private String externalAppRefNumber;
    private String applicationNumber;
    private String status;

    private NextActionDTO nextAction;
    private DataDTO data;
    
    //error DTO 
    private ErrorDetails error;
    private String message;
}
