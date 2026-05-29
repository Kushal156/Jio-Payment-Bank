package com.jpb.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Images_Master", schema = "[BankingJio]")
public class JpbPreOnboardingImagesMasterEntity {

    @Id
    @Column(name = "imageid")
    private String imageid;

    @Column(name = "imagename")
    private String imagename;
}
