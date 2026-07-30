package com.jpb.Entity;

import java.sql.Date;
import java.time.LocalDate;

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
@Table(name = "dmt_customer_master", schema = "[DMT]")
public class DMTCustomerMasterEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dmt_customer_id")
    private Integer dmtCustomerId;

    @Column(name = "dmt_partner_id")
    private Integer dmtPartnerId;

    @Column(name = "jpb_customer_id")
    private String customerId;

    @Column(name = "customer_mobile_no", length = 10)
    private String customerMobileNo;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status")
    private Integer status;

    @Column(name = "kyc_status")
    private Integer kycStatus;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(name = "nsdl_remitterid_id")
    private Integer nsdlRemitteridId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "ovd_data", length = 12)
    private String ovdData;

    @Column(name = "ovd_type", length = 100)
    private String ovdType;

    @Column(name = "last_transaction_date")
    private LocalDate lastTransactionDate;
    
    @Column(name = "aadhaar_token")
    private String aadhaarToken;
    
    @Column(name = "authorization_code")
    private String authorizationCode;
    
    @Column(name = "dob")
    private LocalDate dob;
    
    @Column(name = "aadhar_number", length = 20)
    private String aadharNumber;

    @Column(name = "district", length = 50)
    private String district;

    @Column(name = "address", length = 50)
    private String address;

    @Column(name = "street", length = 50)
    private String street;

    @Column(name = "landmark", length = 50)
    private String landmark;

    @Column(name = "house_no", length = 10)
    private String houseNo;

    @Column(name = "locality", length = 50)
    private String locality;

    @Column(name = "pincode", length = 50)
    private String pincode;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "bank_IFSC", length = 50)
    private String bankIFSC;

    @Column(name = "acc_No", length = 50)
    private String accNo;
    
}
