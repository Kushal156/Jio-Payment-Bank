package com.jpb.DTO;
import lombok.Data;

@Data
public class Aadhaar {
    private String aadhaarNumber;
    private ConsentCode consentCode;

    private String number;

    private String token;
}
