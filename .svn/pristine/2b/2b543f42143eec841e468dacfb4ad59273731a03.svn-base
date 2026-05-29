package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GetConsentDTO {

	private String text1;
    private String status;
    private String fromDate;
    private String toDate;
    private String activityType;
    private String consentTextCode;
    private String mandatory;
    private SalesChannelDTO salesChannel;
    
    //new param for lang
    private String language;
}
