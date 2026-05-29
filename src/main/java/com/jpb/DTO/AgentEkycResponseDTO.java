package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentEkycResponseDTO {

	private String applicationNumber;
    private String status;
    private String message;

    private NextActionDTO nextAction;
    private ErrorDetails error;

}
