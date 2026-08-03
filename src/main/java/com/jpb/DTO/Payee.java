package com.jpb.DTO;
import lombok.Data;

@Data
public class Payee {


    private Mobile mobile;

    private int type;

    private String userId;

    private String bankId;

    private String bankName;

    private Aadhaar aadhaar;

}
