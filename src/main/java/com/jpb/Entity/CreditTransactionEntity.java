package com.jpb.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
public class CreditTransactionEntity {

	@Id
	@Column(name = "[updateStatus]")
	private String updateStatus;
}
