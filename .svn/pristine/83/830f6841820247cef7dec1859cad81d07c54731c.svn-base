package com.jpb.Entity;

import lombok.Data;
import lombok.ToString;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_refund_history", schema = "[BankingJio]")
@Data
@ToString
public class CustomerRefundHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refund_history_id")
    private Integer refundHistoryId;

    @Column(name = "master_id", nullable = false)
    private Integer masterId;

    @Column(name = "external_app_ref_no", length = 50)
    private String externalAppRefNo;

    @Column(name = "application_num", length = 50)
    private String applicationNum;

    @Column(name = "application_type", length = 20)
    private String applicationType;

    @Column(name = "application_sub_type", length = 20)
    private String applicationSubType;

    @Column(name = "api_name", length = 50)
    private String apiName;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "remarks", length = 100)
    private String remarks;

    @Column(name = "action_type", length = 20)
    private String actionType;

    @Column(name = "action_sub_type", length = 20)
    private String actionSubType;

    @Column(name = "next_action_type", length = 20)
    private String nextActionType;

    @Column(name = "next_action_sub_type", length = 20)
    private String nextActionSubType;

    @Column(name = "jio_agent_id", length = 20)
    private String jioAgentId;

    @Column(name = "generated_date_time")
    private LocalDateTime generatedDateTime;

    @Column(name = "vkid", length = 9)
    private String vkid;
}