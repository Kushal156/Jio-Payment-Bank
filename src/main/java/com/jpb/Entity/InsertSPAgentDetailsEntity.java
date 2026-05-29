package com.jpb.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

@Entity
@Data
@ToString
public class InsertSPAgentDetailsEntity {

	@Id
	@Column(name = "agent_id")
	private Integer agentId;
	
	@Column(name = "message")
	private String message;
}
