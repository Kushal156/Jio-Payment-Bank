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
public class GuardianDTO {

	private String firstName;
    private String middleName;
    private String lastName;
    //New params
    private String salutation;
    private String gender;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date dateOfBirth;
    private String relationship;
    
    private List<ContactDetailsDTO> contactDetails;
    private List<OVDetailsDTO> ovdDetails;
    private List<AddressDTO> address;
}
