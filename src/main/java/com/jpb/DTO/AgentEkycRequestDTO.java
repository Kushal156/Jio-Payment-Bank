package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentEkycRequestDTO {

	private String applicationNumber;     // from Save Agent response
    private String apiVersion;
    private String applicationType;
    private String applicationSubType;
    private String initiatingEntityId;
    private String app;

    private ActionDTO action;
    private List<AuthenticateEkycDTO> authenticateList;
    private List<ConsentDTO> consents;
	
}
