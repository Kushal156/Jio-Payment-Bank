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
public class NomineeDTO {

	private String relationship;
	private String salutation;
	private String firstName;
	private String middleName;
	private String lastName;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date dateOfBirth;
	private String gender;
	private Integer percentage;
	private String priority;
	private boolean minor;
	
	private List<ContactDetailsDTO> contactDetails;
	private List<OVDetailsDTO> ovdDetails;
	private List<AddressDTO> address;
	private List<GuardianDTO> guardian;
}
