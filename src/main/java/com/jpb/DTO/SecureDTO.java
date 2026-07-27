package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecureDTO {

	private String encryptionKey;
	
	//DMT-EKYC params
	private Biometrics biometrics;
	private DeviceInfo deviceInfo;
	public void setDeviceInfo(DMTDeviceInfoDTO deviceInfo2) {
		// TODO Auto-generated method stub
		
	}
	public void setDeviceInfo(DeviceInfo deviceInfo2) {
		// TODO Auto-generated method stub
		
	}
}
