package com.jpb.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "agent_master", schema = "[BankingJio]")
@Data
public class AgentMasterEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_id")
    private Long agentId;

    @Column(name = "vkid")
    private String vkid;

    @Column(name = "application_number")
    private String applicationNumber;

    @Column(name = "external_app_ref_number")
    private String externalAppRefNumber;

    @Column(name = "api_version")
    private String apiVersion;

    @Column(name = "application_type")
    private String applicationType;

    @Column(name = "application_sub_type")
    private String applicationSubType;

    @Column(name = "initiating_entity_id")
    private String initiatingEntityId;

    @Column(name = "app")
    private String app;

    @Column(name = "action_type")
    private String actionType;

    @Column(name = "action_sub_type")
    private String actionSubType;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "person_type")
    private String personType;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "name_as_per_pan")
    private String nameAsPerPan;

    @Column(name = "pan_number")
    private String panNumber;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "aadhar_no")
    private String aadharNo;

    @Column(name = "address_type_id")
    private String addressTypeId;

    @Column(name = "care_of")
    private String careOf;

    @Column(name = "house_number")
    private String houseNumber;

    @Column(name = "street")
    private String street;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "locality")
    private String locality;

    @Column(name = "city")
    private String city;

    @Column(name = "post_office")
    private String postOffice;

    @Column(name = "district_id")
    private String districtId;

    @Column(name = "sub_district_id")
    private String subDistrictId;

    @Column(name = "state_id")
    private String stateId;

    @Column(name = "state_code")
    private String stateCode;

    @Column(name = "country")
    private String country;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "contact_type")
    private String contactType;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "contact_status")
    private String contactStatus;

    @Column(name = "email")
    private String email;

    @Column(name = "product_type")
    private String productType;

    @Column(name = "instrument_id")
    private String instrumentId;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "status")
    private String status;

    @Column(name = "next_action_type")
    private String nextActionType;

    @Column(name = "next_action_sub_type")
    private String nextActionSubType;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "jio_agent_id")
    private String jioAgentId;

}