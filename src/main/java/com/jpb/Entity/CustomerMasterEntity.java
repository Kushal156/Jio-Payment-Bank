package com.jpb.Entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "customer_master", schema = "[BankingJio]")
@Data
public class CustomerMasterEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "external_app_ref_number", length = 50)
    private String externalAppRefNumber;

    @Column(name = "application_number", length = 50)
    private String applicationNumber;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "action_sub_type", length = 50)
    private String actionSubType;

    @Column(name = "application_type", length = 50)
    private String applicationType;

    @Column(name = "application_sub_type", length = 50)
    private String applicationSubType;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "organization_name", length = 10)
    private String organizationName;

    @Column(name = "mobile_no", length = 10)
    private String mobileNo;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "jio_agent_id", length = 50)
    private String jioAgentId;

    @Column(name = "latitude", length = 10)
    private String latitude;

    @Column(name = "longitude", length = 10)
    private String longitude;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "next_action_type", length = 50)
    private String nextActionType;

    @Column(name = "next_action_sub_type", length = 50)
    private String nextActionSubType;

    @Column(name = "stage")
    private Integer stage;

    @Column(name = "consent_codes", length = 50)
    private String consentCodes;

    @Column(name = "pan_no", length = 50)
    private String panNo;

    @Column(name = "name_on_pan", length = 50)
    private String nameOnPan;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "source_of_income", length = 10)
    private String sourceOfIncome;

    @Column(name = "annual_salary", length = 10)
    private String annualSalary;

    @Column(name = "occupation", length = 10)
    private String occupation;

    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @Column(name = "updated_date_time")
    private LocalDateTime updatedDateTime;
    
    @Column(name = "aadhar_no")
    private String aadharNo;
    
    @Column(name = "card_opted")
    private String cardOpted;
     
    @Column(name = "transaction_id")
    private String transactionId;
    
    @Column(name = "transaction_status")
    private String transactionStatus;
    
    @Column(name = "transaction_datetime")
    private LocalDateTime transactionTime;
    
    @Column(name = "vkid")
    private String vkid;
    
    @Column(name = "jio_status")
    private String jioStatus;
}
