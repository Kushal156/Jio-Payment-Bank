package com.jpb.DTO;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import jakarta.validation.constraints.*;

@Data
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerInputRequestDTO {
	
	@Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
	private String mobileNumber;
	private String latitude;
	private String longitude;
	private String applicationNumber;
	private String externalAppRefNumber;
	private String language;
	private String pincode;
	private String initiatingEntityId;
	private String martialStatus;
	
	@Pattern(regexp = "\\d{9}", message = "VKID must be 9 digits")
	private String vkid;
	
	@Pattern(regexp = "\\d{4,6}", message = "OTP must be 4-6 digits")
	private String otp;
	
//	@Pattern(regexp = "\\d{12}", message = "Aadhar must be 12 digits")
	private String aadharNo;
	
	@Pattern(regexp = "[A-Z]{5}[0-9]{4}[A-Z]{1}", message = "Invalid PAN format")
	private String panNo;
	
	@Email(message = "Invalid email format")
	private String emailId;
	private String bioMetricData;
	private List<ConsentDTO> consents;
	private FinancialDetailsDTO financialDetails;
	
	private NomineeDTO nomineeDetails;
	private AddressDTO nomineeAddress;
	private List<ContactDetailsDTO> nomineeContactDetails;
	private OVDetailsDTO nomineeOVDDetails;
	
	private GuardianDTO guardianDetails;
	private AddressDTO guardianAddress;
	private List<ContactDetailsDTO> guardianContactDetails;
	private OVDetailsDTO guardianOVDDetails;
	
	private DebitCardDetailsDTO addOn;
	
	//New added for Submit application
	private List<AddressDTO> personAddress;
	private List<FamilyMemberDTO> familyMembers;
	private List<FamilyMemberDTO> familyDetails;
	
	//Refund Params
	private String voucherCode;
	
	
}
