package com.jpb.Entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Data
@Entity
@ToString
@Table(name = "wallet_accounts_master")
public class WalletMasterEntity {

	@Id
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "vk_id")
    private String vkId;

    @Column(name = "sub_service_id")
    private Integer subServiceId;

    @Column(name = "particular")
    private String particular;

    @Column(name = "amount", precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(name = "d_c")
    private String dc;

    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance;

    @Column(name = "transaction_reference_id")
    private String transactionReferenceId;

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "request_date_time")
    private LocalDateTime requestDateTime;

    @Column(name = "deposit_date")
    private LocalDate depositDate;

    @Column(name = "sub_sub_service_id")
    private Integer subSubServiceId;

    @Column(name = "service_id")
    private Integer serviceId;

    @Column(name = "opening_balance", precision = 15, scale = 2)
    private BigDecimal openingBalance;

    @Column(name = "wallet_id")
    private Integer walletId;

    @Column(name = "sub_sub_sub_service_id")
    private Integer subSubSubServiceId;

    @Column(name = "bcid")
    private String bcid;

    @Column(name = "rrn_number")
    private String rrnNumber;

    @Column(name = "transaction_authentication_type")
    private Integer transactionAuthenticationType;

    @Column(name = "transaction_type")
    private Integer transactionType;

    @Column(name = "narration")
    private String narration;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "settlement_type")
    private Integer settlementType;
}
