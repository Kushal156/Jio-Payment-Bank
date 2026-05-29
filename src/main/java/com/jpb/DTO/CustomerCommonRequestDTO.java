package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerCommonRequestDTO {

	private String initiatingEntityId;
	private String applicationNumber;
	private String externalAppRefNumber;
}
