package com.jpb.DTO;

import lombok.Data;

@Data
public class ConsentCode {

    private String id;

    private String description;

    private String version;

    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "IST")
    private String timeStamp;
}
