package com.jpb.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AepsCommonResponseDto {

    private String status;

    private String message;

    private String statusCode;

    private DataDTO data;
}
