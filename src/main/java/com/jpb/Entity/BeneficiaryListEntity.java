package com.jpb.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Entity
public class BeneficiaryListEntity {

    @Id
    @Column(name = "beneficiaryid")
    private Long beneficiaryId;

    @Column(name = "beneficiarymobilenumber", nullable = false)
    private String beneficiaryMobileNumber;

    @Column(name = "beneficiaryname")
    private String beneficiaryName;

    @Column(name = "accountnumber")
    private String accountNumber;

    @Column(name = "ifscode")
    private String ifscCode;

    @Column(name = "beneficiarystatus")
    private Integer beneficiaryStatus;

    @Column(name = "impsstatus")
    private Integer impsStatus;

    @Column(name = "bankName")
    private String bankName;

//    @Column(name = "bank_name")
//    private String bankNameAlt;

    @Column(name = "customer_mobile_no")
    private String customerMobileNo;

//    @Column(name = "customerMobileNo")
//    private String customerMobileNoAlt;

    @Column(name = "dmt_recipient_id")
    private Long dmtRecipientId;
}
