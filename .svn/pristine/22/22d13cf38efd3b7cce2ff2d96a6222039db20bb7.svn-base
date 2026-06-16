package com.jpb.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "resend_voucher_history", schema = "[BankingJio]")
public class ResendVoucherHistoryEntity {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
	
	@Column(name = "master_id")
	private Integer masterId;
	
	@Column(name = "mobile_no", length = 10)
    private String mobileNo;

	@Column(name = "external_app_ref_number", length = 50)
    private String externalAppRefNumber;

    @Column(name = "application_number", length = 50)
    private String applicationNumber;
    
    @Column(name = "customer_external_app_ref_number", length = 50)
    private String customerExternalAppRefNumber;

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

    @Column(name = "latitude", length = 20)
    private String latitude;

    @Column(name = "longitude", length = 20)
    private String longitude;
    
    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "next_action_type", length = 50)
    private String nextActionType;

    @Column(name = "next_action_sub_type", length = 50)
    private String nextActionSubType;
    
    @Column(name = "request_payload")
    private String requestPayload;
    
    @Column(name = "response_payload")
    private String responsePayload;
    
    @Column(name = "vkid")
    private String vkid;
    
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;
}
