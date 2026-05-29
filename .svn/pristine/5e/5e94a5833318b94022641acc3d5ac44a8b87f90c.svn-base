package com.jpb.DTO;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PreOnboardingrequestDto {

	@NotBlank(message = "VKID/Application ID is required")
	@Pattern(regexp = "^(\\d{6}|[a-zA-Z0-9]{9})$", message = "Enter valid 6 digit Application ID or 9 character VKID")
	private String vkid;

	private Integer id;

	private String imageoneLat;

	private String imageTwoLat;

	private String imageThreeLat;

	private String imageFourLat;

	private String imageFiveLat;

	private String imageSixLat;

	private String imageoneLog;

	private String imageTwoLog;

	private String imageThreeLog;

	private String imageFourLog;

	private String imageFiveLog;

	private String imageSixLog;

	private MultipartFile image1;

	private MultipartFile image2;

	private MultipartFile image3;

	private MultipartFile image4;

	private MultipartFile image5;

	private MultipartFile image6;

	private MultipartFile image7;

	private MultipartFile image8;

}
