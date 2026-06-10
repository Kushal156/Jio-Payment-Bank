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
@Table(name = "customer_refund_master", schema = "[BankingJio]")
public class CustomerRefundMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "external_app_ref_number", nullable = false, length = 50)
    private String externalAppRefNumber;

    @Column(name = "application_number", length = 50)
    private String applicationNumber;

    @Column(name = "mobile_no", length = 10)
    private String mobileNo;

    @Column(name = "voucher_code", length = 50)
    private String voucherCode;

    @Column(name = "latitude", length = 20)
    private String latitude;

    @Column(name = "longitude", length = 20)
    private String longitude;

    @Column(name = "vkid", length = 10)
    private String vkid;

    @Column(name = "action_type", length = 50)
    private String actionType;

    @Column(name = "action_sub_type", length = 50)
    private String actionSubType;

    @Column(name = "next_action_type", length = 50)
    private String nextActionType;

    @Column(name = "next_action_sub_type", length = 50)
    private String nextActionSubType;

    @Column(name = "stage")
    private Integer stage;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "jio_agent_id", length = 50)
    private String jioAgentId;

    @Column(name = "master_external_app_ref_number", length = 50)
    private String masterExternalAppRefNumber;

    @Column(name = "master_id")
    private Integer masterId;

    @Column(name = "reject_reason", length = 50)
    private String rejectReason;

    @Column(name = "amount", length = 15)
    private String amount;

    @Column(name = "application_type", length = 50)
    private String applicationType;

    @Column(name = "application_sub_type", length = 50)
    private String applicationSubType;

    @Column(name = "voucher_status", length = 50)
    private String voucherStatus;

    @Column(name = "create_date_time")
    private LocalDateTime createDateTime;

    @Column(name = "update_date_time")
    private LocalDateTime updateDateTime;
    
    @Column(name = "jio_transaction_id")
    private String jioTransactionId;
    
    @Column(name = "vak_transaction_id")
    private String vakTransactionId;
    
    @Column(name = "vak_credit_status")
    private String vakCreditStatus;
}
