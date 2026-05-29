package com.jpb.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;


@Entity
@Data
@Table(name = "pre_onboarding_check_history", schema = "[BankingJio]")
public class PreOnboardingCheckStatusEntity {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @ToString.Exclude
	    @ManyToOne
	    @JoinColumn(name = "pre_onboarding_id", referencedColumnName = "id")    
	    private OnboardingCheckEntity preOnboardingCheck;

	    @ToString.Exclude
	    @ManyToOne
	    @JoinColumn(name = "image_id",  referencedColumnName = "imageid")
	    private JpbPreOnboardingImagesMasterEntity imagesMaster;

	    private Integer status = 0;
  
	    
	    private String remarks;
	    
	    private String latitude;
	    
	    
	    private String longitude;
	}
