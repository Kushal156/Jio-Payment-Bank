package com.jpb.DTO;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionDataDTO {

	private String id;
	private String code;
	private String key;
	private String value; 
	private String category;
	private String description;
	private String createdBy;
	private String updatedBy;
	
	private List<Map<String, Object>> parsedValue;
}
