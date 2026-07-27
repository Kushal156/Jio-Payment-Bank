package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UidaiDataDTO {

		private String authCode;
	 	private String ret;
	    private String code;
	    private Object poa;
	    private String txn;
	    private POIDTO poi;
	    private String pht;
	    private String ts;
	    private String token;
}
