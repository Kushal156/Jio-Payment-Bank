package com.jpb.Entity;

import lombok.Data;
import lombok.ToString;

import java.util.List;

import com.jpb.DTO.AddressDTO;
import com.jpb.DTO.ConsentDTO;
import com.jpb.DTO.ContactDetailsDTO;
import com.jpb.DTO.CustomerInputRequestDTO;
import com.jpb.DTO.DebitCardDetailsDTO;
import com.jpb.DTO.FinancialDetailsDTO;
import com.jpb.DTO.GuardianDTO;
import com.jpb.DTO.NomineeDTO;
import com.jpb.DTO.OVDetailsDTO;

import jakarta.persistence.*;

@Data
@Entity
@Table(name = "pre_onboarding_check", schema = "[BankingJio]")
public class OnboardingCheckEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	private int VKID;
	
	private String latitude;
	
	private String longitude;

	private String status;
	
	private String approvedby;
	
	private String remark;
	
	 @OneToMany(mappedBy = "preOnboardingCheck")
	private List<PreOnboardingCheckStatusEntity> historylist;
}
