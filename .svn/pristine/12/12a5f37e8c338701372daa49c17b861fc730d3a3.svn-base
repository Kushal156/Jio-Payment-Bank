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
import lombok.ToString;

@Entity
@Data
@ToString
@Table(name = "resend_voucher_master", schema = "[BankingJio]")
public class ResendVocuherMasterEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "mobile_no", length = 10)
    private String mobileNo;
    
    @Column(name = "jio_agent_id", length = 50)
    private String jioAgentId;
    
    @Column(name = "created_date_time")
    private LocalDateTime createdDateTime;

    @Column(name = "updated_date_time")
    private LocalDateTime updatedDateTime;
    
    @Column(name = "vkid")
    private String vkid;
    
    @Column(name = "daily_attempt_count")
    private Integer dailyAttemptCount;

    @Column(name = "total_attempt_count")
    private Integer totalAttemptCount;

    @Column(name = "last_attempt_date")
    private LocalDate lastAttemptDate;
}
