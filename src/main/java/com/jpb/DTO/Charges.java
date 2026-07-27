package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class Charges {
	
	private Double value;
	private Double cgst;
	private Double igst;
	private Double sgst;
	private Double ugst;
	private Double serviceCharge;
	private Double tds;
	private Double tcs;
	private Double type;
}
