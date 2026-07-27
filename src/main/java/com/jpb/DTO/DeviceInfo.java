package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceInfo {

	private String peripheral;
	private HeaderDeviceInfoDTO source;
	private GeoLocationDTO location;
}
