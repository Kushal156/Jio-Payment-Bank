package com.jpb.Entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString
@Table(name = "customer_error_master", schema = "[BankingJio]")
public class ErrorCodeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "error_code", nullable = false, unique = true, length = 50)
	private String errorCode;

	@Column(name = "next_action_type", nullable = false, length = 50)
	private String nextActionType;

	@Column(name = "next_action_sub_type", nullable = false, length = 50)
	private String nextActionSubType;

	@Column(name = "user_message", nullable = false, length = 500)
	private String userMessage;

	@Column(name = "created_date_time", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_date_time")
	private LocalDateTime updatedAt;
	
	@Column(name = "is_dedupe_excluded")
	private Boolean isDedupeExcluded;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

}
