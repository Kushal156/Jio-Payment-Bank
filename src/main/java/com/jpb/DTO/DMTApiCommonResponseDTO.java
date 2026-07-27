package com.jpb.DTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DMTApiCommonResponseDTO {

	private String status;
	private String message;
	private DataDTO data;
	private String authorizationCode;
	
	private String responseCode;
	private String responseMessage;
	private ResponseDataDTO responseData;
	private UidaiDataDTO uidaiData;
	
	private String yearlyLimit;
    private String yearlyCount;
    private String monthlyCount;
    private String dailyLimit;
    private String monthlyLimit;
    private String dailyCount;
	
	private ErrorDetails error;
	
}
