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

@Data
@Entity
@ToString
@Table(name = "customer_refund_api_log", schema = "[BankingJio]")
public class CustomerRefundApiLogEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

    @Column(name = "master_id", nullable = false)
    private Integer masterId;

    @Column(name = "api_name", length = 50)
    private String apiName;

    @Column(name = "request_payload", columnDefinition = "varchar(max)")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "varchar(max)")
    private String responsePayload;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "trace_id", length = 50)
    private String traceId;

    @Column(name = "error_message", columnDefinition = "varchar(max)")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
