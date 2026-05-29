package com.jpb.DTO;



import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FamilyMemberDTO {

	private String relationship;

	private String salutation;

	private String firstName;

	private String middleName;

	private String lastName;

	private String dateDfBirth;

	private String gender;

	private List<AddressDTO> address;

}
