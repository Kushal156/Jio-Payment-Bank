package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class DMTDeviceInfoDTO {
	
	private String peripheral;
	private DMTDeviceSource source;
	private GeoLocationDTO location;
}
