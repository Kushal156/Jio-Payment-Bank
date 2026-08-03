package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MiniStatementDto {


    private String date;

    private String txnType;

    private String narration;

    private BigDecimal amount;

    private String transactionType;

    private String transactionTime;

    private String transactionDetails;

}
