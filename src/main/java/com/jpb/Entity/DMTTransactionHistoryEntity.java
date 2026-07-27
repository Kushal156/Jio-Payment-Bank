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
@ToString
@Data
@Table(name = "dmt_transaction_history", schema = "[dmt]")
public class DMTTransactionHistoryEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dmt_transaction_history_id")
    private Long dmtTransactionHistoryId;

    @Column(name = "dmt_transaction_id")
    private Long dmtTransactionId;

    @Column(name = "dmt_transaction_details_id")
    private Long dmtTransactionDetailsId;

    @Column(name = "status")
    private Integer status;

    @Column(name = "request_status")
    private Integer requestStatus;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "date_time")
    private LocalDateTime dateTime;
}
