package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DMTDeviceSource {
	
	private String type;
	private String id;
	private String ip;
	private String osType;
	private String osVer;
	private String model;
}
