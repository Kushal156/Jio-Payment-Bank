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
@Data
@ToString
@Table(name = "dmt_transaction_master", schema = "[DMT]")
public class DMTTransactionMasterEntity {

	@Id
	@Column(name = "dmt_transaction_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long dmtTransactionId;

	@Column(name = "client_reference_id", length = 50)
	private String clientReferenceId;

	@Column(name = "dmt_partner_id")
	private Integer dmtPartnerId;

	@Column(name = "vkid", length = 50)
	private String vkid;

	@Column(name = "dmt_customer_id")
	private String dmtCustomerId;

	@Column(name = "dmt_recipient_id")
	private Long dmtRecipientId;

	@Column(name = "amount")
	private String amount;

	@Column(name = "dmt_transfer_mode")
	private Integer dmtTransferMode;

	@Column(name = "bank_reference_no", length = 100)
	private String bankReferenceNo;

	@Column(name = "tid", length = 50)
	private String tid;

	@Column(name = "split_tid", length = 100)
	private String splitTid;

	@Column(name = "batch_id", length = 100)
	private String batchId;

	@Column(name = "vl_wallet_balance")
	private Double vlWalletBalance;

	@Column(name = "date_time")
	private LocalDateTime dateTime;

	@Column(name = "txn_wallet", length = 100)
	private String txnWallet;

	@Column(name = "session_id", length = 100)
	private String sessionId;

	@Column(name = "service_charge")
	private Double serviceCharge;

	@Column(name = "gst")
	private Double gst;

	@Column(name = "gross_amount")
	private Double grossAmount;

	@Column(name = "rrn_id", length = 100)
	private String rrnId;

	@Column(name = "remarks", length = 255)
	private String remarks;

	@Column(name = "public_ip_address", length = 50)
	private String publicIpAddress;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "device_type", length = 50)
	private String deviceType;

	@Column(name = "app_version", length = 20)
	private String appVersion;

}
