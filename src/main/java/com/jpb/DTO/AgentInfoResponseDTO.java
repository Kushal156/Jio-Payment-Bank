package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentInfoResponseDTO {

	private String message;
	private ErrorDetails error;

	private String externalUserId;
    private String organizationName;
    private String loginId;
    private String name;
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer gender;
    private String dob;
    private Long mobileNumber;
    private String emailId;
    private Integer countryCode;
    private Integer status;
    private boolean adminUser;
    private boolean ekycStatus;
    private String stateCode;
    private String city;
    private Long pinCode;
    
    private AgentInfo agent;
    private AgentInfoAadhaarDTO aadhaar;
    private AddressDTO address;
    
    private List<String> roles;
}
