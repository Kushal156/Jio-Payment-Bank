package com.jpb.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.jpb.Config.TokenManager;
import com.jpb.DTO.AccountsDTO;
import com.jpb.DTO.ActionDTO;
import com.jpb.DTO.AddOnDTO;
import com.jpb.DTO.AddressDTO;
import com.jpb.DTO.AgentInfoResponseDTO;
import com.jpb.DTO.ApplicationStatusResponseDTO;
import com.jpb.DTO.BCDetailsDTO;
import com.jpb.DTO.ContactDetailsDTO;
import com.jpb.DTO.CustomerCommonRequestDTO;
import com.jpb.DTO.CustomerEAuthResponseDTO;
import com.jpb.DTO.CustomerInputRequestDTO;
import com.jpb.DTO.CustomerPanAadharVerifyResponseDTO;
import com.jpb.DTO.DebitCardDetailsDTO;
import com.jpb.DTO.ErrorDetails;
import com.jpb.DTO.FinancialDetailsDTO;
import com.jpb.DTO.GuardianDTO;
import com.jpb.DTO.NomineeDTO;
import com.jpb.DTO.OVDetailsDTO;
import com.jpb.DTO.OrganizationDTO;
import com.jpb.DTO.PersonDTO;
import com.jpb.DTO.PersonalDetailsDTO;
import com.jpb.DTO.ProductDTO;
import com.jpb.DTO.RecallApplicationRequestDTO;
import com.jpb.DTO.ResendOtpResponseDTO;
import com.jpb.DTO.SubmitApplicationRequstDTO;
import com.jpb.DTO.SubmitApplicationResponseDTO;
import com.jpb.Entity.AgentMasterEntity;
import com.jpb.Entity.CustomerApiLogEntity;
import com.jpb.Entity.CustomerHistoryEntity;
import com.jpb.Entity.CustomerMasterEntity;
import com.jpb.Entity.DebitTransactionEntity;
import com.jpb.Repository.AgentMasterRepository;
import com.jpb.Repository.CustomerApiLogRepository;
import com.jpb.Repository.CustomerHistoryRepository;
import com.jpb.Repository.CustomerMasterRepository;
import com.jpb.Repository.DebitTransactionRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class TestService {

	@Value("${generateOtpURL}")
	private String URL;

	@Value("${PublicKeyPath}")
	private String keyPath;

	@Value("${PublicKey}")
	private String PublicKey;

	// Static Values
	@Value("${channelID}")
	private String channelId;

	@Value("${agentId}")
	private String agentID;

	@Value("${applicationType}")
	private String applicationType;

	@Value("${applicationSubType}")
	private String applicationSubType;

	@Value("${oranizationName}")
	private String oranizationName;

	@Value("${countryCode}")
	private String countryCode;

	@Value("${apiVersion}")
	private String apiVersion;

	@Value("${contanctType}")
	private String contanctType;

	@Value("${country}")
	private String country;
	
	@Value("${agentInfoURL}")
	private String agentInfoUrl;
	
	@Value("${applicationStatusURL}")
	private String applicationStatusURL;

	@Autowired
	TokenManager tokenManager;

	@Autowired
	AuthServiceImpl auth;

	@Autowired
	RestTemplate rest;

	@Autowired
	UtilityService util;

	@Autowired
	CustomerMasterRepository masterRepo;

	@Autowired
	CustomerApiLogRepository apiLogRepo;

	@Autowired
	CustomerHistoryRepository historyRepo;

	@Autowired
	AgentMasterRepository agentRepo;

	@Autowired
	DebitTransactionRepository debitRepo;
	
	// Get Consents Mocked
	public ResponseEntity<?> getConsents(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {

		try {

			Map<String, Object> finalResponse = new LinkedHashMap<>();
			Map<String, Object> response = new LinkedHashMap<>();
			List<Map<String, Object>> consents = new ArrayList<>();

			boolean isEnglish = "eng".equalsIgnoreCase(input.getLanguage());

			// Common method to build consent
			BiFunction<String, String, Map<String, Object>> buildConsent = (code, text) -> {
				Map<String, Object> consent = new LinkedHashMap<>();
				consent.put("activityType", "CO_VKYC");
				consent.put("consentTextCode", code);
				consent.put("fromDate", "2023-03-08");
				consent.put("language", isEnglish ? "EN" : "OTH");
				consent.put("mandatory", "Y");

				Map<String, String> salesChannel = new HashMap<>();
				salesChannel.put("id", "7245");

				consent.put("salesChannel", salesChannel);
				consent.put("status", "ACTIVE");
				consent.put("text1", text);
				consent.put("toDate", "2030-10-24");

				return consent;
			};

			// English texts
			if (isEnglish) {

				consents.add(buildConsent.apply("C05",
						"I hereby give my consent to Jio Payments Bank Ltd to send/share messages."));

				consents.add(buildConsent.apply("C06",
						"I agree to the terms published on the mobile application and website."));

				consents.add(buildConsent.apply("C32",
						"I consent to Jio Payments Bank Limited for opening and sharing KYC details."));

			}
			// Other language texts (sample Hindi/Tamil mix)
			else {

				consents.add(buildConsent.apply("C05", "मैं जियो पेमेंट्स बैंक को संदेश भेजने के लिए सहमति देता हूं।"));
				consents.add(buildConsent.apply("C06", "मैं नियमों और शर्तों से सहमत हूं।"));

				consents.add(buildConsent.apply("C32", "मैं केवाईसी विवरण साझा करने के लिए सहमति देता हूं।"));
			}

			response.put("consents", consents);
			response.put("timeStamp", LocalDateTime.now().toString());

			finalResponse.put("code", 0);
			finalResponse.put("response", response);
			finalResponse.put("status", "SUCCESS");

			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {
			log.error("Get Consents Exception", e);
			throw new RuntimeException("Get Consents failed", e);
		}
	}
	
	//Pan Aadhaar Verify Mock
	public ResponseEntity<?> verify(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {
		
		log.info("Customer Aadhaar Pan Verify Mocked Service!!!!");
		String responseBody = null;
		ObjectMapper mapper = new ObjectMapper();
		CustomerPanAadharVerifyResponseDTO finalResponse = new CustomerPanAadharVerifyResponseDTO();
		
		responseBody = """
{
    "externalAppRefNumber": "JPBV1780038054148",
    "applicationNumber": "7245JPBV178003805414816744",
    "status": "SUCCESS",
    "nextAction": {
        "type": "APPLICATION",
        "subType": "SUBMIT"
    },
    "data": {
        "persons": {
            "dbtRecords": [
                {
                    "aadhaarStatus": "A",
                    "bankName": "State Bank of India",
                    "bankIIN": "123456",
                    "npciRefId": "213921321"
                }
            ],
            "aadhaar": {
                "type": "token",
                "value": "01001255NpfpXkUpoji21wLpFhCLm0F/lN/Tes4/AOYfBwpUYdXxCLzXNcRq0dd8ieBUT1zh",
                "maskedAadhaar": "XXXXXXXX4372",
                "name": "Kushal Sardar",
                "dob": "15-03-2000",
                "address": {
                    "line1": "21/5",
                    "line2": "Kalitala More",
                    "line3": "Goalapara Road,Belgharia",
                    "addressType": "PERMANENT",
                    "houseNumber": "21/5",
                    "street": "Goalapara Road",
                    "landmark": "Kalitala More",
                    "locality": "",
                    "city": "Kamarhati (m)",
                    "postOffice": "Belgharia",
                    "district": "North 24 Parganas",
                    "state": "West Bengal",
                    "country": "India",
                    "pincode": "700056",
                    "sameAsPermanent": false
                },
                "photo": "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",
                "gender": "M",
                "timestamp": "2026-05-29T12:33:58.577+05:30",
                "authCode": "a2f84291ffad42f8b8b8e9fef0f35a21",
                "authMode": "FINGER",
                "saTxnId": "SKJIOPAYB1:2026-05-29T07:01:58.754425500Z"
            },
            "financialDetails": {
                "panNumber": "LYPPS1933L",
                "panStatus": "E",
                "aadhaarSeedStatus": "Y",
                "nameStatus": "N",
                "dobStatus": "N"
            }
        }
    }
}
				""";
		
		finalResponse = mapper.readValue(responseBody, CustomerPanAadharVerifyResponseDTO.class);
						
				  if ("SUCCESS".equalsIgnoreCase(finalResponse.getStatus()) && finalResponse.getData() != null
			                && finalResponse.getData().getPersons() != null
			                && finalResponse.getData().getPersons().getAadhaar() != null
			                && finalResponse.getData().getPersons().getAadhaar().getAddress() != null) {

					  Map<String, String> addressLines = util.buildAddressLines(finalResponse);
					  
			            AddressDTO address = finalResponse.getData()
			                    .getPersons()
			                    .getAadhaar()
			                    .getAddress();

			            address.setLine1(addressLines.get("line1"));
			            address.setLine2(addressLines.get("line2"));
			            address.setLine3(addressLines.get("line3"));

			            log.info("Final Address Line1 :: {}", address.getLine1());
			            log.info("Final Address Line2 :: {}", address.getLine2());
			            log.info("Final Address Line3 :: {}", address.getLine3());
			        }
		
		return ResponseEntity.ok(finalResponse);
	}
	
	// Customer E-Auth Mock
	public ResponseEntity<?> customerAuth(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {
		
		log.info("Customer E-Auth Mocked Service!!!!");
		CustomerEAuthResponseDTO finalResponse = new CustomerEAuthResponseDTO();
		finalResponse.setApplicationNumber(input.getApplicationNumber());
		finalResponse.setExternalAppRefNumber(input.getExternalAppRefNumber());
		finalResponse.setStatus("SUCCESS");
		
		return ResponseEntity.ok(finalResponse);
	}

	// Submit Application Mock
	public ResponseEntity<?> submitApp(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {
		log.info("Submit Application Request from Customer :: {}", input.toString());
		ObjectMapper mapper = new ObjectMapper();
		log.info("JSON Reqest from Customer Submit Application :: {}", mapper.writeValueAsString(input));
		try {

			// Token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			// Request
			SubmitApplicationRequstDTO request = new SubmitApplicationRequstDTO();

			request.setApplicationNumber(input.getApplicationNumber());
			request.setExternalAppRefNumber(input.getExternalAppRefNumber());
			request.setApiVersion(apiVersion);
			request.setApplicationType(applicationType);
			request.setApplicationSubType(applicationSubType);
			request.setInitiatingEntityId(channelId);

			// Action
			ActionDTO action = new ActionDTO();
			action.setType("APPLICATION");
			action.setSubType("SUBMIT");
			request.setAction(action);

			// Organization
			OrganizationDTO org = new OrganizationDTO();
			org.setId(oranizationName);
			request.setOrganization(org);

			// BCDetails
			BCDetailsDTO bc = new BCDetailsDTO();
			bc.setUserId(agentID);
			request.setBcDetails(bc);

			// Persons + Finance
			PersonDTO person = new PersonDTO();
			FinancialDetailsDTO finance = new FinancialDetailsDTO();
			finance.setAnnualSalary("3 lakhs");
			finance.setSourceOfIncome("A04");
			finance.setOccupation("JP1");
			person.setFinancialDetails(finance);
			
			//Address(Permanent + Communication)
			person.setAddress(input.getPersonAddress()); //address added for person
			request.setPersons(Collections.singletonList(person));

			// Products + Accounts + Contact
			ProductDTO product = new ProductDTO();
			product.setProductType(applicationSubType); // savings

			AccountsDTO accounts = new AccountsDTO();
			NomineeDTO nominee = new NomineeDTO();
			nominee.setRelationship("Father");
			nominee.setFirstName("Kushal");
			nominee.setLastName("Sardar");

			List<ContactDetailsDTO> contactDetailsList = new ArrayList<>();
			ContactDetailsDTO mobile = new ContactDetailsDTO();
			mobile.setType("Mobile");
			mobile.setCountryCode(countryCode);
			mobile.setMobileNumber(input.getMobileNumber());
			mobile.setStatus("PreVerified");
			contactDetailsList.add(mobile);

			ContactDetailsDTO email = new ContactDetailsDTO();
			email.setType("Personal Email");
			email.setEmail(input.getEmailId());
			contactDetailsList.add(email);
			nominee.setContactDetails(contactDetailsList);

			OVDetailsDTO ovd = new OVDetailsDTO();
			ovd.setDocumentNumber("111");
			ovd.setDocumentType("AADHAR");
			nominee.setOvdDetails(Collections.singletonList(ovd));

			AddressDTO add = new AddressDTO();
			add.setAddressType("PERMANENT");
			add.setDistrict("Navi Mumbai");
			add.setState("MAHARASHTRA");
			add.setStateCode("MH");
			add.setCountry(country);
			add.setPincode("400701");
			nominee.setAddress(Collections.singletonList(add));

			GuardianDTO guard = new GuardianDTO();
			guard.setFirstName("Mithu");
			guard.setLastName("Sardar");

			List<ContactDetailsDTO> finalguardContact = new ArrayList<>();
			ContactDetailsDTO mobGuardContact = new ContactDetailsDTO();
			mobGuardContact.setType("Mobile");
			mobGuardContact.setCountryCode(countryCode);
			mobGuardContact.setMobileNumber("9123456789");
			mobGuardContact.setStatus("PreVerified");
			finalguardContact.add(mobGuardContact);

			ContactDetailsDTO emailGuardContact = new ContactDetailsDTO();
			emailGuardContact.setType("Personal Email");
			emailGuardContact.setEmail("kushal.sardar@gmail.com");
			finalguardContact.add(email);
			guard.setContactDetails(finalguardContact);

			OVDetailsDTO guardOVD = new OVDetailsDTO();
			guardOVD.setDocumentNumber("111");
			guardOVD.setDocumentType("AADHAR");
			guard.setOvdDetails(Collections.singletonList(guardOVD));

			AddressDTO guardADD = new AddressDTO();
			guardADD.setAddressType("PERMANENT");
			guardADD.setDistrict("Navi Mumbai");
			guardADD.setState("MAHARASHTRA");
			guardADD.setStateCode("MH");
			guardADD.setCountry(country);
			guardADD.setPincode("400701");
			guard.setAddress(Collections.singletonList(guardADD));

			nominee.setGuardian(Collections.singletonList(guard));

			AddOnDTO addOn = new AddOnDTO();
			DebitCardDetailsDTO card = new DebitCardDetailsDTO();
			card.setNetwork("DUMMY");
			card.setRegion("DOMESTIC");
			card.setSubscriptionId(1000);
			card.setCardType("VIRTUAL");
			card.setTierType("TRGWFEE");
			addOn.setDebitCardDetails(Collections.singletonList(card));
			// addOn.setDebitCardDetails(card);
			product.setAddOn(Collections.singletonList(addOn));

			accounts.setNominee(Collections.singletonList(nominee));
			product.setAccounts(Collections.singletonList(accounts));
			request.setProducts(Collections.singletonList(product));

			log.info("Submit Application request :: {}", request.toString());
			log.info("JSON Request for Submit Application :: {}", mapper.writeValueAsString(request));

			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<SubmitApplicationRequstDTO> entity = new HttpEntity<>(request, headers);

//			ResponseEntity<String> response = rest.exchange(URL, HttpMethod.POST, entity, String.class);

//				//------------------------------
//			log.info("Submit Application Raw Response :: {}", response.getBody());
//			return ResponseEntity.ok(response.getBody());
				return null;
		} catch (Exception e) {
			log.error("Submit Application Exception", e);
			throw new RuntimeException("Submit Application failed", e);
		}
	}
	
	//Agent Scheduler Mocked BKP
	public ResponseEntity<?> agentInfo(String agentId, HttpServletRequest httpRequest,String latitude, String longitude) {
		
		 log.info("Agent ID :: {}",	agentId);

		    ObjectMapper mapper = new ObjectMapper();
		    try {
		    	
		    	//Token
		        if (!tokenManager.isAccessTokenValid()) {
		            log.info("Token expired → generating new token");
		            auth.generateToken(httpRequest);
		        }
		                
		        String url = agentInfoUrl + agentId + "?organizationName=" + channelId;     

		        log.info("Final URL :: {}", url);
		        
		        HttpHeaders headers = util.buildHeaders(
		                httpRequest,
		                tokenManager.getAccessToken(),
		                tokenManager.getAppIdentifierToken(),
		                latitude, longitude
		        );

		        HttpEntity<RecallApplicationRequestDTO> entity = new HttpEntity<>(headers);

		        ResponseEntity<String> apiResponse = rest.exchange(url, HttpMethod.GET, entity, String.class);
		        
		        log.info("Agent Info Raw Response :: {}", apiResponse.getBody());
		        
		        AgentInfoResponseDTO finalResponse = mapper.readValue(apiResponse.getBody(), AgentInfoResponseDTO.class);
		        
		        Optional.ofNullable(finalResponse.getExternalUserId())
		        .filter(externalId -> agentId.equalsIgnoreCase(externalId))
		        .ifPresentOrElse(
		            matchedExternalId -> {
		                Optional<AgentMasterEntity> optional = agentRepo.findByExternalAppRefNumber(agentId);

		                optional.ifPresent(agent -> {
		                    agent.setJioAgentId(finalResponse.getLoginId());
		                    agent.setActionType("ONBOARDED");
		                    agent.setActionSubType("DONE");
		                    agent.setUpdatedAt(LocalDateTime.now());
		                    agentRepo.save(agent);

		                    log.info("Details Updated Successfully for External-AppRef No :: {}", matchedExternalId);
		                });
		            },
		            () -> {
		                Optional<AgentMasterEntity> optional = agentRepo.findByExternalAppRefNumber(agentId);

		                optional.ifPresent(agent -> {
		                    agent.setActionType("Jio AgentID Pending");
		                    agent.setActionSubType("ExternalUserId mismatch or missing");
		                    agent.setUpdatedAt(LocalDateTime.now());
		                    agentRepo.save(agent);
		                    
		                    log.info("Details Updated Successfully for Non Agent ID External-AppRef No :: {}", agentId);
		                });
		            }
		        );
		        
		        return ResponseEntity.ok(finalResponse);
		        
		    } catch(Exception e) {
		    	log.error("Agent Info Exception", e);
		        throw new RuntimeException("Agent Info API failed", e);
		    }
	}
	
	//Customer Application Status Scheduler Mocked
	@Scheduled(cron = "0 */60 * * * *") //60 min
	public ResponseEntity<?> applicationStatus() {

		ObjectMapper mapper = new ObjectMapper();
		
		MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // Hardcoded values
        httpRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/124.0");
        httpRequest.setRemoteAddr("127.0.0.1");

		CustomerCommonRequestDTO finalRequest = new CustomerCommonRequestDTO();
		ApplicationStatusResponseDTO finalResponse = new ApplicationStatusResponseDTO();
		ErrorDetails error = new ErrorDetails();
			
			List<CustomerMasterEntity> records = masterRepo.findByNextActionTypeAndNextActionSubType("SUBMITTED", "EXIT");

			if(records.isEmpty()) {
	    		log.info("No Customers found for Jio Onboarding Status.......");
	    		error.setCode("200");
	    		error.setMessage("No Customers found for Jio Onboarding Status");
	    		finalResponse.setError(error);
	    		return ResponseEntity.ok(finalResponse);
	    	}
			
			for(CustomerMasterEntity customers : records) {
				
				try {
					
					// Token
					if (!tokenManager.isAccessTokenValid()) {
						log.info("Token expired → generating new token");
						auth.generateToken(httpRequest);
					}
					
					String externalRefNo = customers.getExternalAppRefNumber();
					String latitude = customers.getLatitude();
					String longitude = customers.getLongitude();
					
					log.info("Processing Customer ID External Ref No :: {}", externalRefNo);
					
					finalRequest.setExternalAppRefNumber(externalRefNo);
					finalRequest.setInitiatingEntityId(channelId);
					
					HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
							tokenManager.getAppIdentifierToken(), latitude, longitude);
					
					HttpEntity<CustomerCommonRequestDTO> entity = new HttpEntity<>(finalRequest, headers);
					
					ResponseEntity<String> response = null;
					String responseBody = null;
					Integer statusCode = null;

					
					try {
						response = rest.exchange(applicationStatusURL, HttpMethod.POST, entity, String.class);
						log.info("Application Status Raw Response :: {}", response.getBody());
						responseBody = response.getBody();
						statusCode = response.getStatusCode().value();
					
					} catch (HttpStatusCodeException ex) {
						statusCode = ex.getStatusCode().value();
						responseBody = ex.getResponseBodyAsString();
						log.error("API error: {}, body: {}", statusCode, responseBody);
					
					} catch (ResourceAccessException ex) {
						statusCode = 408;
						responseBody = "Timeout: " + ex.getMessage();
						log.error("API timeout", ex);
					
					} catch (Exception ex) {
						statusCode = 500;
						responseBody = "Unexpected error: " + ex.getMessage();
						log.error("API failure", ex);
					}
					
					if (statusCode != null && statusCode == 200 && responseBody != null) {
						try {
							finalResponse = mapper.readValue(responseBody, ApplicationStatusResponseDTO.class);
							
							//Success
							if("SUCCESS".equalsIgnoreCase(finalResponse.getStatus())) {
								customers.setJioStatus("");
								
							} else {
								//Failure
								customers.setJioStatus("");
							}
							
						} catch (Exception e) {
							error.setCode("500");
							error.setMessage("Response parsing failed");
							finalResponse.setError(error);
							finalResponse.setStatus("FAILED");
						}
					} else {
					
						error.setCode(String.valueOf(statusCode));
						error.setMessage(responseBody != null ? responseBody : "API Failed");
						finalResponse.setStatus("FAILED");
						finalResponse.setError(error);
					}
					
				} catch(Exception e) {
					log.error("Application Status Exception", e);
					throw new RuntimeException("Application Status failed", e);
				}
			}
			return ResponseEntity.ok(finalResponse);
	}

}
