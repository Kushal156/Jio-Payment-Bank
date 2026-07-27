package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData {

	private TransactionAeps transaction;
    private Amount account;
	private String  traceid;
}
