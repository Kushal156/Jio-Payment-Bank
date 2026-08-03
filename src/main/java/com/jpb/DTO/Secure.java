package com.jpb.DTO;

import lombok.Data;

@Data
public class Secure {
    private Biometrics biometrics;

    private DeviceInfo deviceInfo;

    private String latitude;

    private String longitude;

    private String authenticationToken;

    private String encryptionKey;

}
