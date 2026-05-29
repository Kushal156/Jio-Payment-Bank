package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {

	private String productType; // AGENT
    private List<PaymentInstrumentDTO> paymentInstruments;
    
    //Submit application
    private List<AccountsDTO> accounts;
    private List<AddOnDTO> addOn;

}
