package com.jpb.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;

@Entity(name = "NsdlPanVerification")
@Data
@ToString
@Table(name = "nsdl_pan_verification", schema = "[NextGen]")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "NsdlPanVerification.findAll", query = "SELECT n FROM NsdlPanVerification n"),
		@NamedQuery(name = "NsdlPanVerification.findByPanId", query = "SELECT n FROM NsdlPanVerification n WHERE n.panId = :panId"),
		@NamedQuery(name = "NsdlPanVerification.findByPanNumber", query = "SELECT n FROM NsdlPanVerification n WHERE n.panNumber = :panNumber"),
		@NamedQuery(name = "NsdlPanVerification.findByLastName", query = "SELECT n FROM NsdlPanVerification n WHERE n.lastName = :lastName"),
		@NamedQuery(name = "NsdlPanVerification.findByFirstName", query = "SELECT n FROM NsdlPanVerification n WHERE n.firstName = :firstName"),
		@NamedQuery(name = "NsdlPanVerification.findByMiddleName", query = "SELECT n FROM NsdlPanVerification n WHERE n.middleName = :middleName"),
		@NamedQuery(name = "NsdlPanVerification.findByTitle", query = "SELECT n FROM NsdlPanVerification n WHERE n.title = :title"),
		@NamedQuery(name = "NsdlPanVerification.findByPanUpdateDate", query = "SELECT n FROM NsdlPanVerification n WHERE n.panUpdateDate = :panUpdateDate"),
		@NamedQuery(name = "NsdlPanVerification.findByPanStatus", query = "SELECT n FROM NsdlPanVerification n WHERE n.panStatus = :panStatus"),
		@NamedQuery(name = "NsdlPanVerification.findByDateTime", query = "SELECT n FROM NsdlPanVerification n WHERE n.dateTime = :dateTime"),
		@NamedQuery(name = "NsdlPanVerification.findByNameAsPerPancard", query = "SELECT n FROM NsdlPanVerification n WHERE n.nameAsPerPancard = :nameAsPerPancard"),
		@NamedQuery(name = "NsdlPanVerification.findByAadhaarSeedingStatus", query = "SELECT n FROM NsdlPanVerification n WHERE n.aadhaarSeedingStatus = :aadhaarSeedingStatus"),
		@NamedQuery(name = "NsdlPanVerification.findByFatherName", query = "SELECT n FROM NsdlPanVerification n WHERE n.fatherName = :fatherName") })
public class NSDLPanVerificationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pan_id")
	private Integer panId;

	@Size(max = 20)
	@Column(name = "pan_number")
	private String panNumber;
	
	@Size(max = 250)
	@Column(name = "last_name")
	private String lastName;
	
	@Size(max = 250)
	@Column(name = "first_name")
	private String firstName;
	
	@Size(max = 250)
	@Column(name = "middle_name")
	private String middleName;
	
	@Size(max = 50)
	@Column(name = "title")
	private String title;
	
	@Size(max = 50)
	@Column(name = "pan_update_date")
	private String panUpdateDate;
	
	@Size(max = 1)
	@Column(name = "pan_status")
	private String panStatus;
	
	@Column(name = "date_time")
	@Temporal(TemporalType.TIMESTAMP)
	private LocalDateTime dateTime;
	
	@Size(max = 100)
	@Column(name = "name_as_per_pancard")
	private String nameAsPerPancard;
	
	@Size(max = 5)
	@Column(name = "aadhaar_seeding_status")
	private String aadhaarSeedingStatus;
	
	@Size(max = 100)
	@Column(name = "father_name")
	private String fatherName;
}
