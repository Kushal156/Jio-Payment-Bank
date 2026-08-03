package com.jpb.Entity;

import java.math.BigDecimal;
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
@Table(name = "dmt_transaction_details", schema = "[dmt]")
public class DMTTransactionDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dmt_transaction_details_id")
	private Long dmtTransactionDetailsId;

	@Column(name = "dmt_transaction_id")
	private Long dmtTransactionId;

	@Column(name = "tid")
	private String tid;

	@Column(name = "bank_reference_no")
	private String bankReferenceNo;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "fee")
	private BigDecimal fee;

	@Column(name = "time_stamp")
	private LocalDateTime timeStamp;

	@Column(name = "date_time")
	private LocalDateTime dateTime;

	@Column(name = "client_reference_id")
	private String clientReferenceId;

	@Column(name = "vkid")
	private String vkid;

	@Column(name = "sms_flag")
	private String smsFlag;

	@Column(name = "txn_wallet")
	private String txnWallet;

	@Column(name = "service_charge")
	private Double serviceCharge;

	@Column(name = "gst")
	private BigDecimal gst;

	@Column(name = "gross_amount")
	private Double grossAmount;

	@Column(name = "rrn_id")
	private String rrnId;

	@Column(name = "benename")
	private String benename;

	@Column(name = "remarks")
	private String remarks;
}
