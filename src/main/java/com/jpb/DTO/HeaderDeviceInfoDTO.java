package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeaderDeviceInfoDTO {

	private String ipAddress;
	private String type;
	private String os;
	private String appName;
	private String appId;
	private String sdkVersion;
	private String mobile;
	private String userAgent;
	
	//new params for customer Onboarding
	private GeoLocationDTO location;
	
	//DMT Params
	private String id;
	private String ip;
	private String osType;
	private String osVer;
	private String model;

}
