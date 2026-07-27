package com.jpb.DTO;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Users {
	
	private Mobile mobile;
	private String emailAddress;
	private String entityType;
	
	//DMT Register Params
	private DataDTO name;
	private Integer occupationCode;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date dob;
	private List<AddressDTO> address;
	
	//AEPS Params
	private String userId;
	private BankDetails bankDetails;

}
