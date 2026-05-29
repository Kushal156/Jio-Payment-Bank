package com.jpb.DTO;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AuthenticateEkycDTO {

	private AadhaarDTO aadhaar;
    private String value; 
}
