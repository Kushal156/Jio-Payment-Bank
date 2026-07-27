package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Amount {

	private String netAmount;	
	private String grossAmount;
	private String balance;
	private String accountExist;
	private List<Charges> charges;
	
}
