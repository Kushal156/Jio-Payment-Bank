package com.jpb.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Mobile {

	private String mobileNumber;
	private String countryCode;
	private String number;
}
