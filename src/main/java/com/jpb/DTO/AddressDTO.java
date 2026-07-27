package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AddressDTO {

    private String addressType; 
    private String careOf;
    private String houseNumber;
    private String street;
    private String landmark;
    private String locality;
    private String city;
    private String postOffice;
    private String district;
    private String subDistrict;
    private String state;
    private String stateCode;
    private String country;
    private String pincode;
    
    //New fields
    private String line1;
    private String line2;
    private String line3;
    private Boolean sameAsPermanent;

    private GeoLocationDTO geoLocation;
    
    //DMT Params
    private String pinCode;

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

}
