package com.jpb.DTO;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class Method {

	private Integer type;
	private Integer subType;
}
