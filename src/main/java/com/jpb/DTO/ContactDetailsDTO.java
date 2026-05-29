package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ContactDetailsDTO {

	private String type; // Mobile / Personal Email
    private String countryCode;
    private String mobileNumber;
    private String status;
    private String email;
}
