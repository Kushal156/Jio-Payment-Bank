package com.jpb.DTO;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RecallApplicationRequestDTO {

	private String applicationNumber;

    private String apiVersion;
    private String applicationType;
    private String applicationSubType;
    private String initiatingEntityId;
    private String applicationStatus;
    private String app;

    private ActionDTO action;
    private String applicationStatusReasonDescription;
}
