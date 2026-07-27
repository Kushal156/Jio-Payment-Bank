package com.jpb.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;

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
@Table(name = "dmt_recipient_master", schema = "[DMT]")
public class DMTAddBeneficiaryEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dmt_recipient_id")
    private Long dmtRecipientId;

    @Column(name = "dmt_customer_id")
    private String dmtCustomerId;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "partner_recipient_id")
    private String partnerRecipientId;

    @Column(name = "nbin_code")
    private String nbinCode;

    @Column(name = "ifsc")
    private String ifsc;

    @Column(name = "account_no")
    private String accountNo;

    @Column(name = "account_type")
    private Integer accountType;

    @Column(name = "recipient_mobile_no")
    private String recipientMobileNo;

    @Column(name = "vkid")
    private String vkid;

    @Column(name = "is_verified")
    private Integer isVerified;

    @Column(name = "status")
    private Integer status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "amount_transferred_flag")
    private Integer amountTransferredFlag;

    @Column(name = "bank_customer_name")
    private String bankCustomerName;

    @Column(name = "ifsc_status")
    private String ifscStatus;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "tid")
    private String tid;

    @Column(name = "client_ref_id")
    private String clientRefId;

    @Column(name = "verification_failure_refund")
    private Integer verificationFailureRefund;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "fee")
    private BigDecimal fee;

    @Column(name = "is_name_editable")
    private Integer isNameEditable;

    @Column(name = "is_Ifsc_required")
    private Integer isIfscRequired;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "rrn_id")
    private String rrnId;

    @Column(name = "gst")
    private BigDecimal gst;

    @Column(name = "gross_amount")
    private Double grossAmount;

    @Column(name = "description")
    private String description;

    @Column(name = "dmt_partner_id")
    private Integer dmtPartnerId;

    @Column(name = "email_id")
    private String emailId;
}
