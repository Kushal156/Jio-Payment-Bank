package com.jpb.DTO;

import java.time.Instant;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionDMT {

	private String idempotentKey;
	private Integer currency;
	private String invoice;
	private Method method;
	private Integer mode;
	private Metadata metadata;
	private Integer captureMethod;
	private String livemode;
	private Integer application;
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "IST")
	private Instant initiatingEntityTimestamp;
	private InitiatingEntity initiatingEntity; 	
	
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "IST")
	private Instant transactionTime;
	private String rrn;
	private String transactionId;
	
	//DMT params
	private String methodType;
	private String methodSubType;
	private String nextActionRequest;
	private Double amount;
	private Double charges;	
	
	private UidaiDataDTO uidaiData;
	
	@JsonFormat(pattern = "EEE MMM dd HH:mm:ss z yyyy", locale = "en")
	private ZonedDateTime timestamp;
	private String status;	
}
