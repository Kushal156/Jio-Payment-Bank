package com.jpb.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatusCode;

@Entity
@Table(name = "customer_api_log", schema = "[BankingJio]")
@Data
public class CustomerApiLogEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Integer logId;

	@Column(name = "master_id")
	private Integer masterId;

    @Column(name = "api_name", length = 20)
    private String apiName;

    @Column(name = "request_payload", columnDefinition = "VARCHAR(MAX)")
    private String requestPayload;

    @Column(name = "response_payload", columnDefinition = "VARCHAR(MAX)")
    private String responsePayload;

    @Column(name = "status_code")
    private Integer statusCode;

    @Column(name = "trace_id", length = 20)
    private String traceId;

    @Column(name = "error_message", length = 50)
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "error_code")
    private String errorCode;
    
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
