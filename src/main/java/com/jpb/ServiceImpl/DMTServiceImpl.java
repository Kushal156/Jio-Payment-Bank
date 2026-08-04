package com.jpb.ServiceImpl;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import com.jpb.Config.TokenManager;
import com.jpb.DTO.AadhaarDTO;
import com.jpb.DTO.AddressDTO;
import com.jpb.DTO.DMTTransactionRequestDto;
import com.jpb.DTO.Agent;
import com.jpb.DTO.AgentUser;
import com.jpb.DTO.Amount;
import com.jpb.DTO.AuthenticateDTO;
import com.jpb.DTO.Biometrics;
import com.jpb.DTO.Charges;
import com.jpb.DTO.ConsentDTO;
import com.jpb.DTO.DMTApiCommonResponseDTO;
import com.jpb.DTO.DMTBeneValidationRequestDTO;
import com.jpb.DTO.DMTDeviceInfoDTO;
import com.jpb.DTO.DMTDeviceSource;
import com.jpb.DTO.DMTRegisterUserRequestDTO;
import com.jpb.DTO.DataDTO;
import com.jpb.DTO.DmtCommonResponseDto;
import com.jpb.DTO.DmtCommonrequestDto;
import com.jpb.DTO.ErrorDetails;
import com.jpb.DTO.GenerateOtpResponseDTO;
import com.jpb.DTO.GeoLocationDTO;
import com.jpb.DTO.InitiatingEntity;
import com.jpb.DTO.JpbDmtGenerateOtpRequestDto;
import com.jpb.DTO.JpbDmtVerifyOtpRequestDto;
import com.jpb.DTO.Metadata;
import com.jpb.DTO.Method;
import com.jpb.DTO.Mobile;
import com.jpb.DTO.OrganizationDTO;
import com.jpb.DTO.PayerDto;
import com.jpb.DTO.SecureDTO;
import com.jpb.DTO.TransactionDMT;
import com.jpb.DTO.Users;
import com.jpb.Entity.AgentMasterEntity;
import com.jpb.Entity.BeneficiaryListEntity;
import com.jpb.Entity.DMTAddBeneficiaryEntity;
import com.jpb.Entity.DMTCustomerMasterEntity;
import com.jpb.Entity.DMTTransactionDetailsEntity;
import com.jpb.Entity.DMTTransactionHistoryEntity;
import com.jpb.Entity.DMTTransactionMasterEntity;
import com.jpb.Entity.DebitTransactionEntity;
import com.jpb.Entity.WalletMasterEntity;
import com.jpb.Repository.AgentMasterRepository;
import com.jpb.Repository.BeneficiaryListRepository;
import com.jpb.Repository.DMTAddBeneficiaryRepository;
import com.jpb.Repository.DMTCustomerMasterRepository;
import com.jpb.Repository.DMTTransactionDeatilRepository;
import com.jpb.Repository.DMTTransactionHistoryRepository;
import com.jpb.Repository.DMTTransactionMasterRepository;
import com.jpb.Repository.DebitTransactionRepository;
import com.jpb.Repository.WalletMasterRepository;
import com.jpb.Service.DMTService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class DMTServiceImpl implements DMTService {

	// Helper Services
	@Autowired
	TokenManager tokenManager;

	@Autowired
	AuthServiceImpl auth;

	@Autowired
	UtilityService util;

	@Autowired
	EncryptionService encrypt;

	@Autowired
	RestTemplate rest;
	
	@Value("${serviceID}")
	private String serviceID;

	@Value("${SubServiceID}")
	private String subServiceID;

	@Value("${virtual.SubSubServiceID}")
	private String subSubServiceID;
	
	@Value("${SubSubSubServiceID}")
	private String subSubSubServiceID;

	@Value("${walletID}")
	private String walletID;

	@Value("${TransactionAuthenticationType}")
	private String transactionAuthenticationType;

	@Value("${TransactionType}")
	private String transactionType;

	//URL's
	@Value("${checkMobURL}")
	private String checkMobURL;

	@Value("${countryCode}")
	private String countryCode;

	@Value("${DMTOTPURL}")
	private String DMTOTPURL;

	@Value("${DMTEKYCURL}")
	private String DMTEKYCURL;
	
	@Value("${RegisterUserURL}")
	private String RegisterUserURL;
	
	@Value("${DMTTransactionURL}")
	private String DMTTransactionURL;
	
	@Value("${DMTStatusCheckURL}")
	private String StatusCheckURL;
	
	@Value("${CustomerLimitURL}")
	private String CustomerLimitURL;
	
	@Value("${DMTTransactionHistoryURL}")
	private String DMTTransactionHistoryURL;
	
	@Value("${BeneValidateURL}")
	private String BeneValidateURL;

	//Static
	@Value("${channelID}")
	private String channelID;
	
	@Value("${PublicKeyPath}")
	private String keyPath;
	
	@Value("${partnerId}")
	private Integer partnerId;
	
	@Autowired
	DMTCustomerMasterRepository customerMasterRepo;
	
	@Autowired
	AgentMasterRepository agentRepo;
	
	@Autowired
	DMTAddBeneficiaryRepository addBeneRepo;
	
	@Autowired
	DMTTransactionDeatilRepository DMTDeatilRepo;
	
	@Autowired
	DMTTransactionHistoryRepository DMTHistoryRepo;
	
	@Autowired
	DMTTransactionMasterRepository DMTMasterRepo;
	
	@Autowired
	WalletMasterRepository walletRepo;
	
	@Autowired
	DebitTransactionRepository debitRepo;
	
	@Autowired
	BeneficiaryListRepository beneListRepo;
	
	@Autowired
	JdbcTemplate jdbc;

	//Check customer mob-no
	@Override
	public ResponseEntity<?> checkmobileNo(DmtCommonrequestDto input, HttpServletRequest httpRequest) {

		ObjectMapper mapper = new ObjectMapper();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		ErrorDetails error = new ErrorDetails();
		Integer statusCode = null;
		finalResponse.setEkycCharges("Please Charge Rs.10/- from customer for EKYC");
		
		try {

			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<Void> requestentity = new HttpEntity<>(header);
			String finalURL = checkMobURL + "/" + input.getMobile();

			log.info("Request URL :: {}", finalURL);
			
			try {
				response = rest.exchange(finalURL, HttpMethod.GET, requestentity, String.class);
				log.info("JSON Raw Response for DMT Check Mobile-No :: {}", response.getBody());
				
				if(response != null) {
					
//					if(true) {
//					responseBody = """
//							{
//									"status": "SUCCESS",
//									"data": {
//									"remittanceUserId": "19741027047510982778",
//									"aadharToken":
//									"01001255v4BssQzpOZ8ofbYEwd2ltwfaCn7Hhx7RYta8JsK/UnFQhswksj0Cc",
//									"firstName": "Prakash Vishwambhar",
//									"lastName": "Choudhary"
//									}
//							}
//							""";
//					statusCode = 200;
					
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {
				
				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);
			
				finalResponse.setError(error);

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
					
					finalResponse = mapper.readValue(responseBody, DMTApiCommonResponseDTO.class);
					
				} catch (Exception e) {
					error.setCode("500");
					error.setMessage("Response parsing failed");
					finalResponse.setError(error);
					finalResponse.setStatus("FAILURE");
				}
			} else {

				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setStatus("FAILURE");
				finalResponse.setError(error);
			}
			
			//DB Activity ----------------------------------
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getStatus())) {
				
				finalResponse.setMessage("Customer Exists with JPB");
				
				boolean update = false;
				
				//Customer Master
				DMTCustomerMasterEntity masterEntity = customerMasterRepo
			            .findByCustomerMobileNoAndDmtPartnerId(input.getMobile(), partnerId)
			            .orElse(new DMTCustomerMasterEntity());	
				
				boolean isNew = masterEntity.getDmtCustomerId() == null;
				
				if(isNew) {
					 masterEntity.setCustomerMobileNo(input.getMobile());
				     masterEntity.setDmtPartnerId(partnerId);
				     masterEntity.setStartDate(LocalDate.now());
				     masterEntity.setStatus(1);
				     masterEntity.setCustomerName(finalResponse.getData().getFirstName() + " " + finalResponse.getData().getLastName());
				     masterEntity.setAadhaarToken(finalResponse.getData().getAadharToken());
				     masterEntity.setCustomerId(finalResponse.getData().getRemittanceUserId());
				     masterEntity.setDescription("VAK in SYNC with JPB");
				     update = true;
				}
				
				//Update + Compare Customer Remitter ID
				if(!Objects.equals(masterEntity.getCustomerId(), finalResponse.getData().getRemittanceUserId())) {
					masterEntity.setCustomerId(finalResponse.getData().getRemittanceUserId());
					update = true;
				}
				
				//Update + Compare Aadhar Token 
				if(!Objects.equals(masterEntity.getAadhaarToken(), finalResponse.getData().getAadharToken())) {
					masterEntity.setAadhaarToken(finalResponse.getData().getAadharToken());
					update = true;
				}
				
				if(update) {
					customerMasterRepo.save(masterEntity);
				}
			}
			else if("FAILURE".equalsIgnoreCase(finalResponse.getStatus()) && finalResponse.getError() != null
					&& "User does not exist.".equalsIgnoreCase(finalResponse.getError().getMessage())) {
				
				finalResponse.setMessage(finalResponse.getError().getMessage());
				
				DMTCustomerMasterEntity masterEntity = customerMasterRepo
			            .findByCustomerMobileNoAndDmtPartnerId(input.getMobile(), partnerId)
			            .orElse(new DMTCustomerMasterEntity());	
				
				 masterEntity.setCustomerMobileNo(input.getMobile());
			     masterEntity.setDmtPartnerId(partnerId);
			     masterEntity.setStatus(0);
			     masterEntity.setDescription("User Does not Exists in JPB");
			     
			     customerMasterRepo.save(masterEntity);
			}
			
			return ResponseEntity.ok(finalResponse);

		} catch (HttpClientErrorException e) {

			log.error("DMT Check Mobile-No Exception", e);

			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}

	//Generate OTP
	@Override
	public ResponseEntity<?> generateDmtOtp(DmtCommonrequestDto input, HttpServletRequest requesthttp) {

		ObjectMapper mapper = new ObjectMapper();
		JpbDmtGenerateOtpRequestDto dmtrequests = new JpbDmtGenerateOtpRequestDto();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Integer statusCode = null;
		String agentId = null;

		try {
			
			if(input.getMobile() == null || "".equalsIgnoreCase(input.getMobile()) || input.getMobile().length() !=10) {
				finalResponse.setMessage("Mobile Number is neccessary");
				finalResponse.setStatus("Failure");
				return ResponseEntity.ok(finalResponse);
			}	
			
			Optional<AgentMasterEntity> agentEntity = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid());
			if (agentEntity.isPresent()) {
				AgentMasterEntity agent = agentEntity.get();
				agentId = agent.getJioAgentId();
				log.info("Agent Master Details for the VKID :: {}, {}", input.getVkid(), agent.toString());
			}

			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(requesthttp);
			}

			// Request
			Users user = new Users();
			Mobile mobile = new Mobile();

			mobile.setMobileNumber(input.getMobile());
			mobile.setCountryCode(countryCode);
			user.setMobile(mobile);
			user.setEmailAddress(input.getEmailAddress());
			user.setEntityType("3");

			dmtrequests.setUser(user);

			dmtrequests.setScope("REQUEST");
			dmtrequests.setExtraInfo("");
			dmtrequests.setPurpose("9");
			
			Optional.ofNullable(input.getNetAmount())
			.ifPresentOrElse(amt -> {
				dmtrequests.setAmount(amt);
			}, 
			() -> {
				dmtrequests.setAmount("0.00");
			});
			
			dmtrequests.setExtraInfo(agentId);

			AuthenticateDTO authenticate = new AuthenticateDTO();
			authenticate.setMode(33);
			authenticate.setValue("");
			authenticate.setAction("generate");

			dmtrequests.setAuthenticateList(Collections.singletonList(authenticate));

			log.info("JSON Request for DMT Generate OTP :: {}", mapper.writeValueAsString(dmtrequests));

			HttpHeaders header = util.buildHeaders(requesthttp, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<JpbDmtGenerateOtpRequestDto> entity = new HttpEntity<>(dmtrequests, header);

			try {
				response = rest.exchange(DMTOTPURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for DMT Generate OTP :: {}", response.getBody());

				if (response != null) {
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

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

					finalResponse = mapper.readValue(responseBody, DMTApiCommonResponseDTO.class);

				} catch (Exception e) {
					error.setCode("500");
					error.setMessage("Response parsing failed");
					finalResponse.setError(error);
					finalResponse.setStatus("FAILURE");
				}
			} else {

				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setStatus("FAILURE");
				finalResponse.setError(error);
			}
			
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getStatus())) {
				finalResponse.setMessage("OTP sent Successfully");
			} else if (finalResponse.getError() != null) {
				finalResponse.setMessage(finalResponse.getError().getMessage());
			} else {
				finalResponse.setMessage("Generate Mobile-OTP Exception");
			} 
			
			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {
			log.error("DMT Generate Mobile-OTP Exception", e);
			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}

	//Verify OTP
	@Override
	public ResponseEntity<?> verifyDmtOtp(DmtCommonrequestDto input, HttpServletRequest httpRequest) {

		JpbDmtVerifyOtpRequestDto request = new JpbDmtVerifyOtpRequestDto();
		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Integer statusCode = null;
		String agentId = null;
		
		try {
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
			Optional<AgentMasterEntity> agentEntity = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid());
			if (agentEntity.isPresent()) {
				AgentMasterEntity agent = agentEntity.get();
				agentId = agent.getJioAgentId();
				log.info("Agent Master Details for the VKID :: {}, {}", input.getVkid(), agent.toString());
			}
			
			String publicKey = new String(Files.readAllBytes(Paths.get(keyPath))).trim();
			String encryptionKey = encrypt.generateRandomString(16);
			String finalKey = encrypt.encryptRSA(encryptionKey, publicKey);
			String encryptedOTP = encrypt.encryptAES(input.getOTP(), encryptionKey);
			
			//Request
			request.setScope("REQUEST");
			request.setPurpose("9");
			request.setExtraInfo(agentId);
			Optional.ofNullable(input.getNetAmount())
			.ifPresentOrElse(amt -> {
				request.setAmount(amt);
			}, 
			() -> {
				request.setAmount("0.00");
			});
			
			//User
			Users user = new Users();
			Mobile mob = new Mobile();
			mob.setCountryCode(countryCode);
			mob.setMobileNumber(input.getMobile());
			user.setMobile(mob);
			user.setEntityType("3");
			request.setUser(user);
			
			//Authenticate
			AuthenticateDTO auth = new AuthenticateDTO();
			auth.setMode(33);
			auth.setAction("verify");
			auth.setValue(encryptedOTP);
			request.setAuthenticateList(Collections.singletonList(auth));
			
			//Secure
			SecureDTO secure = new SecureDTO();
			secure.setEncryptionKey(finalKey);
			request.setSecure(secure);
			
			log.info("JSON Request for DMT Verify OTP :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<JpbDmtVerifyOtpRequestDto> entity = new HttpEntity<>(request, header);
			
			try {
				response = rest.exchange(DMTOTPURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for Verify OTP :: {}", response.getBody());

				if (response != null) {
					
//				if(true) {	
//					responseBody = """
//							{
//							"status": "SUCCESS",
//							"authorizationCode": "02951952-b166-4191-8898-0c33a207212d"
//							}
//							""";
//					statusCode = 200;
					
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

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
					
					finalResponse = mapper.readValue(responseBody, DMTApiCommonResponseDTO.class);
					
				} catch (Exception e) {
					error.setCode("500");
					error.setMessage("Response parsing failed");
					finalResponse.setError(error);
					finalResponse.setStatus("FAILURE");
				}
			} else {

				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setStatus("FAILURE");
				finalResponse.setError(error);
			}
			
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getStatus())) {
				
				finalResponse.setMessage("OTP Verified Successfully");
				
				final String authCode = finalResponse.getAuthorizationCode();
				
				customerMasterRepo.findByCustomerMobileNoAndDmtPartnerId(input.getMobile(), partnerId)
		        .ifPresent(masterEntity -> {
		        	masterEntity.setAuthorizationCode(authCode);
		        	masterEntity.setDescription("OTP Verified & auth code generated");
		        	customerMasterRepo.save(masterEntity);
		        });			
			} else if (finalResponse.getError() != null && "FAILURE".equalsIgnoreCase(finalResponse.getStatus())) {
				finalResponse.setMessage(finalResponse.getError().getMessage());
			}
			
			return ResponseEntity.ok(finalResponse);
			
		} catch (Exception e) {
			log.error("DMT Verify Mobile-OTP Exception", e);
			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}		
	}

	//EKYC
	@Override
	@Transactional
	public ResponseEntity<?> dmtEkyc(DmtCommonrequestDto input, HttpServletRequest httpRequest) {

//		input.setVkid("RJ2903071");
		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Integer statusCode = null;
		String agentId = null;
		boolean paymentStatus = false;
		String clientRefID = util.generateRRNAndStan();
		log.info("clientRefID for EKYC Transaction :: {}", clientRefID);

		try {
			
			log.info("JSON request from DMT Customer EKYC :: {}", mapper.writeValueAsString(input));
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
			//Debit Wallet before API CALL----------------------------------------
			Optional<DMTTransactionMasterEntity> master = DMTMasterRepo.findByClientReferenceId(clientRefID);
			if(master.isEmpty()) {
				
				// Money Debit----------
				DebitTransactionEntity debit = debitRepo.debitTransaction
						(clientRefID, //unique transaction ID
						"Payment For JPB DMT EKYC", //Remarks
						serviceID,
						subServiceID,
						subSubServiceID, //Sub Sub Service ID 1485-100, 1486-400 
						subSubSubServiceID,
						input.getVkid(),
						walletID,
						10.00, //Amount
						input.getVkid(), //BCID
						clientRefID, //RRN No
						transactionAuthenticationType,
						transactionType
						);
						
				log.info("Debit Transaction Details :: {}", debit.toString());
				paymentStatus = "Y".equalsIgnoreCase(debit.getUpdateStatus()) ? true : false;
				
				if(paymentStatus) {
						log.info("Payment Successful---->");
				} else {
						log.info("Payment Pending---->");
				}
			}
			
			Optional<AgentMasterEntity> agentEntity = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid());
			if (agentEntity.isPresent()) {
				AgentMasterEntity agent = agentEntity.get();
				agentId = agent.getJioAgentId();
				log.info("Agent Master Details for the VKID :: {}, {}", input.getVkid(), agent.toString());
			} else {
				finalResponse.setMessage("Please Onboard Agent first");
				finalResponse.setStatus("FAILURE");
				return ResponseEntity.ok(finalResponse);
			}

			// Request
			DMTTransactionRequestDto request = new DMTTransactionRequestDto();

			String idempotentKey = String.valueOf(System.currentTimeMillis());
			String timestamp = Instant.now().truncatedTo(ChronoUnit.MILLIS).toString();

			// Transaction
			TransactionDMT transaction = new TransactionDMT();
			transaction.setIdempotentKey(idempotentKey);
			transaction.setCurrency(356);
			transaction.setInvoice(idempotentKey);
			transaction.setApplication(Integer.parseInt(channelID));
			transaction.setCaptureMethod(1);
			transaction.setLivemode("true");
			transaction.setInitiatingEntityTimestamp(Instant.now());
			
			// Method
			Method method = new Method();
			method.setSubType(632);
			method.setType(322);
			transaction.setMethod(method);

			// MetaData
			Metadata meta = new Metadata();
			Agent agent = new Agent();
			AgentUser agentUser = new AgentUser();
			agentUser.setId(agentId); // agent ID;
			agent.setAgentUser(agentUser);
			meta.setAgent(agent);
			transaction.setMetadata(meta);
			
			// Initiation Entity
			InitiatingEntity ini = new InitiatingEntity();
			ini.setEntityId(Integer.parseInt(channelID));
			transaction.setInitiatingEntity(ini);
			request.setTransaction(transaction);

			// Payer
			PayerDto payer = new PayerDto();
			Mobile mob = new Mobile();
			AadhaarDTO aadhar = new AadhaarDTO();
			ConsentDTO consent = new ConsentDTO();

			mob.setNumber(input.getMobile());
			mob.setCountryCode(countryCode);
			payer.setMobile(mob);

			aadhar.setAadhaarNumber(input.getAadhaarnumber());
			consent.setId("B88");
			consent.setDescription(
					"I hereby provide my consent to JioPayments Bank Limited (\"Bank\") to use my Aadhaar number and biometric authentication to verify my identity for the purpose "
							+ "of doing AePS transactions from my account (\"Service\"). I have "
							+ "reviewed the transaction details and found to be correct. I "
							+ "understand and agree to the terms and conditions governing the "
							+ "Service as available on website www.jiobank.in and confirm that "
							+ "my biometric authentication be treated as my consent for "
							+ "availing the Service from the Bank. I hereby give my consent to "
							+ "receive promotional content on behalf of the Bank.");
			consent.setVersion("1");
			consent.setTimeStamp(Instant.now());
			aadhar.setConsentCode(consent);
			payer.setAadhaar(aadhar);
			request.setPayer(payer);

			// Secure
			SecureDTO secure = new SecureDTO();
			Biometrics bio = new Biometrics();
			
//			String fingerPrint = """
//					<?xml version="1.0"?>
//					<PidData>
//					  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="40" qScore="98" />
//					  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.5.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIINDdBOTJCMkEwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzI0MDQxNjE4WhcNMjYxMDIyMDQzMTA2WjCBgjEkMCIGCSqGSIb3DQEJARYVc3VwcG9ydEBtYW50cmF0ZWMuY29tMQswCQYDVQQGEwJJTjELMAkGA1UECBMCR0oxEjAQBgNVBAcTCUFobWVkYWJhZDEOMAwGA1UEChMFTVNJUEwxCzAJBgNVBAsTAklUMQ8wDQYDVQQDEwZNRlMxMTAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCjpIjDaBfFBr8NSNKoUWhi2ILuBof3XQwo02SeRGbkFmkF4WkIIpw2IV9wbTkdi+PHmxzH6rm4eONVq/Q2Saz//WiJFcJJg4QZPfZwJ46jg+gqUKYSHXg7KGlcQ1l9Uenj0L64GivUsSbuC9IQqV9U5aqCBe0Odt5Wb2x5YnbLy39g//14DnLKqxuhVnVe0tHpTIh/g/jbOXgHSaCGi/B7EYYc4XEUN7fhWtn94P2VWKBiMdRIycSqsCmHnWIc4qqezEXFH+FNCcuLzfVkOgScLCwiMY9z928LhoWy4LYQjBpFZyPGpIUR7PaQ7UsxRgcpVrUuj09pSQRpgp9ScdtxAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAHGgsJrUfEA/edu0Eap5lvGJjBQqifpf4dC6cQ9cDGxF6pb3h9JIZcdpn6l8S6s9KM11z5wrH04SQ6nMwlqWXvS+r46P3y0OgXcnUZxXiIgCREwrIcuGof1cqYXIfQJ50W8yg11a5AAjR8QmFXXxuCfhQRDIl6qbx/ejgos2DMwhAs1Z17XR3k8Vaw+hTlVS1pslT7XAzMvBU/oN7RdFKaYXWEjncThDdn9AR/pePihvNwG3fhAUPPGrkKKA1rOLtMsZFo26XAmx4Gus9C1Ph7MewtEJtkg5lMsEH8mrj8X/H1QlMPYTmsBWIyd3+A3Didu9+L0JLLs/91cibwHvfYU=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
//					    <additional_info>
//					      <Param name="srno" value="7784312" />
//					      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
//					      <Param name="ts" value="2026-07-24T12:58:38+05:30" />
//					      <Param name="modality_type" value="Finger" />
//					      <Param name="device_type" value="L1" />
//					    </additional_info>
//					  </DeviceInfo>
//					  <Skey ci="20280825">JYR1sGDr8gIL+5GRq3NBgga4bVmQI20q2qtwLwCEkaoBaLVe1taWNHIh/xl40X5yEIc+QXQ7bqtkHk4sLd+lzxXeN3IoypQxGieLt8emhB+E9fWZjjFmLBzUm/tnR1lGYfBA2fEegCIJxLr4+mrFplvier/UIcKImgi7dp8dFFdK22lvgNgH2QzJJD3vyc+VC94FVfiSWapeiWC0WICchkfuQnmDzkkLEzNKmNeJHNEY/8J9L0WjuFD/SyMj0BsZrqQ3IfNeVVgnmB2bWMegoMrUDCClEb42wYIaKWgngOurocr0hPVTqmNoCG9mBW0VXvYP79+xSJloGmMj2sX+hg==</Skey>
//					  <Hmac>BrK7AdVO8VHJEyV0fGPITmbjHW/5dIY1ndrzMuzwJTTzY4XAJIseZc8UN16xXzfX</Hmac>
//					  <Data type="X">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</Data>
//					</PidData>
//					""";
//			String base64String = util.DMTconvertPidXmlToBase64Json(fingerPrint);

			
			String base64String = util.DMTconvertPidXmlToBase64Json(input.getFingerprint());
			if("Not Proper FingerPrint Data".equalsIgnoreCase(base64String)) {
				finalResponse.setStatus("FAILURE");
				finalResponse.setMessage(base64String);	
				return ResponseEntity.ok(finalResponse);
			}
			
			bio.setFingerprint(base64String);
			bio.setType(1);
			secure.setBiometrics(bio);

			// Device
			DMTDeviceInfoDTO deviceInfo = new DMTDeviceInfoDTO();
			DMTDeviceSource source = new DMTDeviceSource();
			source = mapper.readValue(util.getDMTDeviceInfoJson(httpRequest), DMTDeviceSource.class);

			GeoLocationDTO location = new GeoLocationDTO();
			location.setLatitude(input.getLatitude());
			location.setLongitude(input.getLongitude());

			deviceInfo.setLocation(location);
			deviceInfo.setSource(source);
			secure.setDeviceInfo(deviceInfo);

			request.setSecure(secure);

			//Amount
			Amount amt = new Amount();
			amt.setGrossAmount("10"); //Hardcoded as standard
			amt.setNetAmount("10"); //Hardcoded as standard

			Charges charges = new Charges();
			charges.setValue((double) 3);
			charges.setIgst(0.46);
			charges.setCgst((double) 0);
			charges.setSgst((double) 0);
			charges.setUgst((double) 0);
			charges.setServiceCharge(2.54);
			charges.setTds((double) 0);
			charges.setTcs(null);
			charges.setType(null);
			amt.setCharges(Collections.singletonList(charges));
			request.setAmount(amt);
			
			log.info("JSON Request for DMT EKYC :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<DMTTransactionRequestDto> entity = new HttpEntity<>(request, header);
			
			log.info("Entity for DMT EKYC :: {}", entity.toString());
			
			try {
				response = rest.exchange(DMTEKYCURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for DMT EKYC :: {}", response.getBody());

				if (response != null) {
					
//					responseBody = """
//					{
//						  "responseCode": "00",
//						  "responseMessage": "SUCCESS",
//						  "responseData": {
//						    "transaction": {
//						      "transactionId": "10312621052795617000",
//						      "methodType": 0,
//						      "methodSubType": 0
//						    },
//						    "uidaiData": {
//						      "ret": "Y",
//						      "code": "3babcfe548114ffca89e7d3cba43cff0",
//						      "poa": {
//						        "country": "India",
//						        "lm": "milind nagar",
//						        "loc": "1, nidhi niwas chawl gaodevi road",
//						        "pc": "400078",
//						        "street": "",
//						        "dist": "Mumbai",
//						        "state": "Maharashtra",
//						        "lom": "",
//						        "house": "",
//						        "po": ""
//						      },
//						      "txn": "SKJIOPAYB1:2026-07-29T09:09:54.233Z",
//						      "poi": {
//						        "gender": "M",
//						        "dob": "23-12-1999",
//						        "name": "Kishan Mahesh Kushwaha",
//						        "isResidentForeigner": ""
//						      },
//						      "pht": "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",
//						      "ts": "2026-07-29T14:40:01.429+05:30",
//						      "token": "010012555op8j16F+ddUcFEe1zu+VF8D9jA++7o5a9uJGnRp8+sVSmOzLsKd4FijqEMEjJyr"
//						    }
//						  }
//						}
//							""";
//					statusCode = 200;
					
					
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

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
					
					finalResponse = mapper.readValue(responseBody, DMTApiCommonResponseDTO.class);
					
				} catch (Exception e) {
					error.setCode("500");
					error.setMessage("Response parsing failed");
					finalResponse.setError(error);
					finalResponse.setStatus("FAILURE");
				}
			} else {

				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setStatus("FAILURE");
				finalResponse.setMessage(finalResponse.getError().getMessage());
				finalResponse.setError(error);
			}
			
			//DB Activity
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getResponseMessage()) && "00".equalsIgnoreCase(finalResponse.getResponseCode())) {
				
				finalResponse.setMessage("EKYC Data fetched Successfully");
				finalResponse.setStatus("SUCCESS");
				
				final String aadhaarToken = finalResponse.getResponseData().getUidaiData().getToken();
				final String name = finalResponse.getResponseData().getUidaiData().getPoi().getName();
				
				customerMasterRepo.findByCustomerMobileNoAndDmtPartnerId(input.getMobile(), partnerId)
		        .ifPresent(masterEntity -> {
		        	masterEntity.setCustomerName(name);
		        	masterEntity.setKycStatus(1);
		        	masterEntity.setAadhaarToken(aadhaarToken);
		        	masterEntity.setStartDate(LocalDate.now());
		        	masterEntity.setDescription("EKYC Done with JPB");
		        	customerMasterRepo.save(masterEntity);
		        });			
			} else if("02".equalsIgnoreCase(finalResponse.getResponseCode())) {
				finalResponse.setMessage("Invalid Agent User Id");
				finalResponse.setStatus("FAILURE");
			} else {
				finalResponse.setMessage(finalResponse.getResponseMessage());
				finalResponse.setStatus("FAILURE");
			}
			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {
			log.error("DMT EKYC Exception", e);
			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}
	
	//Register User
	@Override
	public ResponseEntity<?> registerUser(DmtCommonrequestDto input, HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Integer statusCode = null;
		String aadharToken = null, authCode = null, custName = null;
		DMTCustomerMasterEntity masterEntity = new DMTCustomerMasterEntity();
		String firstName = null, lastName = null, middleName = null;
		
		try {
			
			log.info("JSON Request for DMT Register user :: {}", mapper.writeValueAsString(input));
			
			Optional<DMTCustomerMasterEntity> masterEntityRecord = customerMasterRepo.findByCustomerMobileNoAndDmtPartnerId(input.getMobile(), partnerId);
			
			if(masterEntityRecord.isPresent()) {
				masterEntity = masterEntityRecord.get();
				aadharToken = masterEntity.getAadhaarToken();
				authCode = masterEntity.getAuthorizationCode();
				custName = masterEntity.getCustomerName();
				
				log.info("Authorization Code :: {}, Aadhaar Token :: {}, Customer Name :: {}", 
						authCode, aadharToken, custName);
				
				if(custName != null && !custName.trim().isEmpty()) {
					String[] parts = custName.trim().split("\\s+");
					
					if(parts.length == 1) {
						firstName = parts[0];
					} else if(parts.length == 2) {
						firstName = parts[0];
						lastName = parts[1];
					} else {
						firstName = parts[0];
						middleName = parts[1];
						lastName = parts[2];
					}
					log.info("firstName :: {}, middleName :: {}, lastName :: {}", firstName, middleName, lastName);
				}
			}
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
			//Request
			DMTRegisterUserRequestDTO request = new DMTRegisterUserRequestDTO();
			request.setAuthorizationCode(authCode); //verifyOTP response 10mins valid
			request.setAadharToken(aadharToken); //aadhaar.token() will get from EKCY response
			
			//Users
			Users user = new Users();
			user.setOccupationCode(3); //HardCoded
			user.setDob(input.getDob());
			
			Mobile mob = new Mobile();
			DataDTO name = new DataDTO();
			AddressDTO add = new AddressDTO();
			
			//Mobile
			mob.setMobileNumber(input.getMobile());
			user.setMobile(mob);
			
			//Name
//			name.setFirstName(firstName);
//			Optional.ofNullable(middleName).ifPresentOrElse(midName -> {
//				name.setMiddleName(midName);
//			}, () -> {
//				name.setMiddleName(null);
//			});
//			name.setLastName(lastName);

			name.setFirstName(input.getFirstName());
			Optional.ofNullable(input.getMiddleName()).ifPresentOrElse(midName -> {
				name.setMiddleName(midName);
			}, () -> {
				name.setMiddleName(null);
			});
			name.setLastName(input.getLastName());
			user.setName(name);
			
			//Address
			add.setCountry("India");
			add.setDistrict(input.getDistrict());
			add.setHouseNumber(input.getHouseNumber());
			add.setLandmark(input.getLandMark());
			add.setLocality(input.getLocality());
			add.setCity(input.getCity());
			add.setPincode(input.getPincode());
			add.setState(input.getState());
			user.setAddress(Collections.singletonList(add));
			
			request.setUser(user);
			
			log.info("JSON Request for DMT Register User :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<DMTRegisterUserRequestDTO> entity = new HttpEntity<>(request, header);
			
			try {
				response = rest.exchange(RegisterUserURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for DMT Register User :: {}", response.getBody());

				if (response != null) {
				
//				if(true) {
//					responseBody = """
//							{
//								"status": "SUCCESS",
//								"data": {
//								"remittanceUserId": "19741027047510982778"
//								}
//							}
//							""";
//					statusCode = 200;
					
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

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
					
					finalResponse = mapper.readValue(responseBody, DMTApiCommonResponseDTO.class);
					
				} catch (Exception e) {
					error.setCode("500");
					error.setMessage("Response parsing failed");
					finalResponse.setError(error);
					finalResponse.setStatus("FAILURE");
				}
			} else {

				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setStatus("FAILURE");
				finalResponse.setError(error);
			}
			
			//DB Activity
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getStatus())) {
				
				finalResponse.setMessage("Customer Registered Successfully");
				
				final String remitterId = finalResponse.getData().getRemittanceUserId();
				customerMasterRepo.findByCustomerMobileNoAndDmtPartnerId(input.getMobile(), partnerId)
		        .ifPresent(custMasterEntity -> {
		        	custMasterEntity.setAadharNumber(input.getAadhaarnumber());
		        	custMasterEntity.setDistrict(input.getDistrict());
		        	custMasterEntity.setHouseNo(input.getHouseNumber());
		        	custMasterEntity.setLandmark(input.getLandMark());
		        	custMasterEntity.setStreet(input.getLandMark());
		        	custMasterEntity.setAddress(input.getAddress());
		        	custMasterEntity.setLocality(input.getLocality());
		        	custMasterEntity.setPincode(input.getPincode());
		        	custMasterEntity.setCity(input.getCity());
		        	custMasterEntity.setState(input.getState());
		        	custMasterEntity.setBankIFSC(input.getBankIFSC());
		        	custMasterEntity.setAccNo(input.getAccNo());        	
		        	custMasterEntity.setDob(input.getDob());
		        	custMasterEntity.setCustomerId(remitterId);
		        	custMasterEntity.setDescription("Customer Onboarded with JPB");
		        	
		        	customerMasterRepo.save(custMasterEntity);
		        });			
			} else if("FAILURE".equalsIgnoreCase(finalResponse.getStatus()) && finalResponse.getError() != null) {
				finalResponse.setMessage(finalResponse.getError().getMessage());
			} else {
				finalResponse.setMessage("DMT EKYC Exception");
			}
			
			return ResponseEntity.ok(finalResponse);
			
		} catch(Exception e) {
			log.error("DMT Register User Exception", e);
			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}
	
	//DMT Transaction
	@Override
	@Transactional
	public ResponseEntity<?> tranx(DmtCommonrequestDto input, HttpServletRequest httpRequest) {

		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Integer statusCode = null, dmtCustID = null;
		String idempotentKey = String.valueOf(System.currentTimeMillis());
		String OTP = input.getOTP();
		String base64OTP = Base64.getEncoder().encodeToString(OTP.getBytes(StandardCharsets.UTF_8));
		String clientRefID = util.generateRRNAndStan(); //Client ref ID
		String agentId = null, remitterId = null, beneName = null, deviceType = "0", os = "WINDOWS", sessionId = null; 
		long recipientId = 0;
		DMTTransactionMasterEntity DMTMaster = new DMTTransactionMasterEntity();
		DMTTransactionHistoryEntity DMTHistory = new DMTTransactionHistoryEntity();
		DMTTransactionDetailsEntity DMTDetails = new DMTTransactionDetailsEntity();
		long tranxID = 0, tranxDetailID = 0;
		boolean paymentStatus = false; // Checking Payment Status Y-Success N-Failure
		String payerMobNo = null;
		String beneAccNo = null, beneIFSC = null, beneFirstName = null, beneMiddleName = null, beneLastName = null;
		
		try {
			
			log.info("JSON request for DMT Transaction from Customer :: {}", mapper.writeValueAsString(input));
			
			String userAgent = httpRequest.getHeader("User-Agent");
			if (userAgent != null) {
				String ua = userAgent.toLowerCase();
				if (ua.contains("android") || ua.contains("iphone") || ua.contains("mobile")) {
					deviceType = "1"; //1-mobile, 0-web
				}
			}
			
			log.info("ClientRefID for DMT Transaction :: {}", clientRefID);
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
			//Agent ID
			Optional<AgentMasterEntity> agentEntity = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid());
			if (agentEntity.isPresent()) {
				AgentMasterEntity agent = agentEntity.get();
				agentId = agent.getJioAgentId();
				log.info("Agent Master Details for the VKID :: {}, {}", input.getVkid(), agent.toString());
			}
			
			//Remitter ID
			Optional<DMTCustomerMasterEntity> masterEntity = customerMasterRepo
		            .findByCustomerMobileNoAndDmtPartnerId(input.getSenderMobileNo(), partnerId);
			if(masterEntity.isPresent()) {
				DMTCustomerMasterEntity cust = masterEntity.get();
				remitterId = cust.getCustomerId();
				dmtCustID = cust.getDmtCustomerId();
				payerMobNo = cust.getCustomerMobileNo();
				log.info("Remitter/Customer ID :: {}, DMT Customer-ID :: {}, Payer MobNo :: {}", remitterId, dmtCustID, payerMobNo);
			}
			
			//Receipient ID
			Optional<DMTAddBeneficiaryEntity> addBeneRecord = addBeneRepo.findByDmtRecipientId(input.getBeneficiaryId());
			if(addBeneRecord.isPresent()) {
				DMTAddBeneficiaryEntity beneMaster = addBeneRecord.get();
				recipientId = beneMaster.getDmtRecipientId();
				beneName = beneMaster.getRecipientName();
				beneAccNo = beneMaster.getAccountNo();
				beneIFSC = beneMaster.getIfsc();
				
				log.info("Receipient ID :: {}, Beneficiary Name :: {}, Beneficiary Acc-No :: {}, Beneficiary IFSC :: {}", recipientId, beneName, beneAccNo, beneIFSC);
				
				if(beneName != null && !beneName.trim().isEmpty()) {
					String[] parts = beneName.trim().split("\\s+");
					
					if(parts.length == 1) {
						beneFirstName = parts[0];
					} else if(parts.length == 2) {
						beneFirstName = parts[0];
						beneLastName = parts[1];
					} else {
						beneFirstName = parts[0];
						beneMiddleName = parts[1];
						beneLastName = parts[2];
					}
					log.info("firstName :: {}, middleName :: {}, lastName :: {}", beneFirstName, beneMiddleName, beneLastName);
				}
			}	
			
//			Optional<DMTAddBeneficiaryEntity> addBeneRecord = addBeneRepo.findByDmtCustomerIdAndDmtPartnerIdAndAccountNo
//					(dmtCustID.toString(), partnerId, input.getAccNo());
//			if(addBeneRecord.isPresent()) {
//				DMTAddBeneficiaryEntity beneMaster = addBeneRecord.get();
//				recipientId = beneMaster.getDmtRecipientId();
//				beneName = beneMaster.getRecipientName();
//				log.info("Receipient ID :: {}, Beneficiary Name :: {}", recipientId, beneName);
//			}	
			
			//Debit Wallet before API CALL----------------------------------------
			Optional<DMTTransactionMasterEntity> master = DMTMasterRepo.findByClientReferenceId(clientRefID);
			if(master.isEmpty()) {
				
				// Money Debit----------
				DebitTransactionEntity debit = debitRepo.debitTransaction
						(clientRefID, //unique transaction ID
						"Payment For JPB DMT", //Remarks
						serviceID,
						subServiceID,
						subSubServiceID, //Sub Sub Service ID 1485-100, 1486-400 
						subSubSubServiceID,
						input.getVkid(),
						walletID,
						Double.parseDouble(input.getNetAmount()), //Amount
						input.getVkid(), //BCID
						clientRefID, //RRN No
						transactionAuthenticationType,
						transactionType
						);
						
				log.info("Debit Transaction Details :: {}", debit.toString());
				paymentStatus = "Y".equalsIgnoreCase(debit.getUpdateStatus()) ? true : false;
				
				if(paymentStatus) {
						log.info("Payment Successful---->");
				} else {
						log.info("Payment Pending---->");
				}
				
				//Transaction Master Table
				DMTMaster.setClientReferenceId(clientRefID);
				DMTMaster.setDmtPartnerId(partnerId);
				DMTMaster.setVkid(input.getVkid());
				DMTMaster.setDmtCustomerId(dmtCustID.toString()); //ID of DMT Customer Master
				DMTMaster.setDmtRecipientId(recipientId);
				DMTMaster.setAmount(input.getNetAmount());
				DMTMaster.setDmtTransferMode(2); // BY Default 2
				DMTMaster.setDateTime(LocalDateTime.now());
				DMTMaster.setSessionId(beneAccNo + beneIFSC + clientRefID);
				DMTMaster.setServiceCharge(10.00); //10.00 by Default ??
				DMTMaster.setGrossAmount(Double.parseDouble(input.getNetAmount()) + 10.00);
				DMTMaster.setPublicIpAddress(httpRequest.getRemoteAddr());
				DMTMaster.setLatitude(input.getLatitude());
				DMTMaster.setLongitude(input.getLongitude());
				DMTMaster.setRemarks("JPB Transaction Initiated");
				DMTMaster.setDeviceType(deviceType);
				
				DMTMaster = DMTMasterRepo.save(DMTMaster);
				tranxID = DMTMaster.getDmtTransactionId();
				
				//Transaction Details Table
				DMTDetails.setDmtTransactionId(tranxID);
				DMTDetails.setAmount(Double.parseDouble(input.getNetAmount()));
				DMTDetails.setDateTime(LocalDateTime.now());
				DMTDetails.setClientReferenceId(clientRefID);
				DMTDetails.setVkid(input.getVkid());
				DMTDetails.setFee(null); // ??
				DMTDetails.setServiceCharge(null); // ??
				DMTDetails.setGrossAmount(null); // ?? will be calculated according to fee+serviceChrg
				DMTDetails.setBenename(beneName);			
				DMTDetails.setRemarks("JPB Transaction Initiated");
				
				DMTDetails = DMTDeatilRepo.save(DMTDetails);
				tranxDetailID = DMTDetails.getDmtTransactionDetailsId();
				
				//Transaction History Table
				DMTHistory.setDmtTransactionId(tranxID);
				DMTHistory.setDmtTransactionDetailsId(tranxDetailID);
				DMTHistory.setRemarks("JPB Transaction Initiated");
				DMTHistory.setDateTime(LocalDateTime.now());
				if(paymentStatus) {
					DMTHistory.setStatus(0);
					DMTHistory.setRequestStatus(8);
				} else {
					DMTHistory.setStatus(6);
					DMTHistory.setRequestStatus(1);
				}
				DMTHistory = DMTHistoryRepo.save(DMTHistory);
				
				if (!paymentStatus) {
				    finalResponse.setResponseCode("02");
				    finalResponse.setStatus("FAILURE");
				    finalResponse.setResponseMessage("FAILURE");
				    finalResponse.setMessage("Money Not Debited from Wallet");
				    return ResponseEntity.ok(finalResponse);
				}
			}
			
			//Request
			DMTTransactionRequestDto request = new DMTTransactionRequestDto();
			
			//Transaction
			TransactionDMT trans = new TransactionDMT();
			trans.setIdempotentKey(idempotentKey);
			trans.setCurrency(356);
			trans.setInvoice(idempotentKey);
			trans.setMode(2);
			trans.setCaptureMethod(1);
			trans.setLivemode("true");
			trans.setApplication(Integer.parseInt(channelID));
			trans.setInitiatingEntityTimestamp(Instant.now());
			
			//Method
			Method meth = new Method();
			meth.setType(425);
			meth.setSubType(630);
			trans.setMethod(meth);
			
			//MetaData
			Metadata meta = new Metadata();			
			//Agent
			Agent agent = new Agent();
			agent.setId("10000000000000005321"); //Have to check if the login id works
			AgentUser agUser = new AgentUser();
			agUser.setId(agentId);
			agent.setAgentUser(agUser);
			
			OrganizationDTO org = new OrganizationDTO();
			org.setId(channelID);
			agent.setOrganization(org);
			
			AddressDTO add = new AddressDTO();
			add.setStateCode("22");//change according to the code
			add.setPincode(input.getPincode());
			agent.setAddress(add);	
			
			InitiatingEntity ini = new InitiatingEntity();
			ini.setEntityId(Integer.parseInt(channelID));
			trans.setInitiatingEntity(ini);
			
			meta.setAgent(agent);
			trans.setMetadata(meta);
			request.setTransaction(trans);	
			
			//Amount
			Amount amt = new Amount();
			Charges charges = new Charges();
			charges.setValue((double) 3);
			charges.setIgst(0.46);
			charges.setCgst((double) 0);
			charges.setSgst((double) 0);
			charges.setUgst((double) 0);
			charges.setServiceCharge(2.54);
			charges.setTds((double) 0);
			charges.setTcs(null);
			charges.setType(null);
			amt.setCharges(Collections.singletonList(charges));
			amt.setGrossAmount(input.getGrossAmount() + 10.00);
			amt.setNetAmount(input.getNetAmount());
			request.setAmount(amt);
			
			//Payer
			PayerDto payer = new PayerDto();
			payer.setUserId(remitterId); // will get from Verify OTP
			payer.setType(13);
			
			Mobile mob = new Mobile();
			mob.setNumber(payerMobNo);
			mob.setCountryCode(countryCode);
			payer.setMobile(mob);
			request.setPayer(payer);
			
			//Payee
			PayerDto payee = new PayerDto();
			payee.setAccountAlias(beneIFSC);
			payee.setBankIfsc(beneIFSC);
			payee.setAccountNumber(beneAccNo);
			payee.setName(input.getFirstName() + input.getLastName());
			request.setPayee(payee);
			
			//Auth
			ConsentDTO auth = new ConsentDTO();
			auth.setConsentCode(base64OTP);
			request.setAuth(auth);
			
			//Secure
			SecureDTO secure = new SecureDTO();
			DMTDeviceInfoDTO deviceInfo = new DMTDeviceInfoDTO();
			deviceInfo.setPeripheral("Jiopay");
			DMTDeviceSource source = new DMTDeviceSource();
			source = mapper.readValue(util.getDMTDeviceInfoJson(httpRequest), DMTDeviceSource.class);
			deviceInfo.setSource(source);				
			secure.setDeviceInfo(deviceInfo);		
			request.setSecure(secure);

			log.info("JSON Request for DMT Transaction :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<DMTTransactionRequestDto> entity = new HttpEntity<>(request, header);
			
			try {
				response = rest.exchange(DMTTransactionURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for DMT Transaction :: {}", response.getBody());

				if (response != null) {
					
					
//					if(true) {
//					responseBody = """
//							{
//"responseCode": "00",
//"responseMessage": "SUCCESS",
//"responseData": {
//"transactionTime": "Tue Sep 03 11:25:01 IST 2024",
//"remitterMobile": "9009911411",
//"beneficiaryName": "Rajesh Mann",
//"beneficiaryAccountNumber": "123456041",
//"beneficiaryBankName": "HDFC0CAMCBK",
//"transactions": [
//{
//"transactionId": "10012424359119646000",
//"rrn": "424316251453",
//"amount": 5000.0,
//"charges": 60.0,
//"timestamp": "Tue Sep 03 11:25:01 IST 2024",
//"status": "SUCCESS"
//}
//]
//}
//}
//							""";
//					statusCode = 200;
					
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

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
					
					finalResponse = mapper.readValue(responseBody, DMTApiCommonResponseDTO.class);
					
				} catch (Exception e) {
					error.setCode("500");
					error.setMessage("Response parsing failed");
					finalResponse.setError(error);
					finalResponse.setStatus("FAILURE");
				}
			} else {
				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setStatus("FAILURE");
				finalResponse.setError(error);
			}
			
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getResponseMessage()) && "00".equalsIgnoreCase(finalResponse.getResponseCode())) {
				
				finalResponse.setMessage("DMT Transaction Successfull");
				finalResponse.setStatus("SUCCESS");
				
				List<TransactionDMT> transaction = finalResponse.getResponseData().getTransactions();
				
				if (transaction != null && !transaction.isEmpty()) {

				    TransactionDMT txn = transaction.get(0);
				    finalResponse.setJPBTransactionID(txn.getTransactionId());
				    finalResponse.setJPBRRN(txn.getRrn());
				    finalResponse.setJPBAmount(txn.getAmount());
				    finalResponse.setJPBCharges(txn.getCharges());
				    finalResponse.setJPBTransactionTime(txn.getTimestamp());
				    

				    //Transaction Master
				    DMTMaster.setBankReferenceNo(txn.getTransactionId());
				    DMTMaster.setTid(txn.getTransactionId());
				    DMTMaster.setRrnId(txn.getRrn());
				    DMTMaster.setRemarks("JPB Status " + txn.getStatus());

				    //Transaction Details
					DMTDetails.setTid(txn.getTransactionId());
				    DMTDetails.setBankReferenceNo(txn.getTransactionId());
				    DMTDetails.setTimeStamp(LocalDateTime.now());
				    DMTDetails.setRemarks(txn.getStatus());
				    
				    //Transaction History
				    DMTHistory.setStatus(0);
				    DMTHistory.setRequestStatus(1);
				    DMTHistory.setRemarks(txn.getStatus());
				    
				    DMTMasterRepo.save(DMTMaster);
				    DMTDeatilRepo.save(DMTDetails);
				    DMTHistoryRepo.save(DMTHistory);
				    
				}
			} else if ("FAILURE".equalsIgnoreCase(finalResponse.getResponseMessage()) || "1000".equalsIgnoreCase(finalResponse.getResponseCode())) {
				
				finalResponse.setMessage(finalResponse.getResponseMessage());
				finalResponse.setStatus("FAILURE");
				
				//Transaction Master
			    DMTMaster.setRemarks("JPB Status Failed");

			    //Transaction Details
			    DMTDetails.setTimeStamp(LocalDateTime.now());
			    DMTDetails.setRemarks("Failed");
			    
			    //Transaction History
			    DMTHistory.setStatus(1);
			    DMTHistory.setRequestStatus(6);
			    DMTHistory.setRemarks("Failed");
			    
			    DMTMasterRepo.save(DMTMaster);
			    DMTDeatilRepo.save(DMTDetails);
			    DMTHistoryRepo.save(DMTHistory);
			}
			else {
				
				finalResponse.setMessage(finalResponse.getResponseMessage());
				finalResponse.setStatus("FAILURE");
				
				//Transaction Master
			    DMTMaster.setRemarks("JPB Status Failed");

			    //Transaction Details
			    DMTDetails.setTimeStamp(LocalDateTime.now());
			    DMTDetails.setRemarks("Failed");
			    
			    //Transaction History
			    DMTHistory.setStatus(1);
			    DMTHistory.setRequestStatus(6);
			    DMTHistory.setRemarks("Failed");
			    
			    DMTMasterRepo.save(DMTMaster);
			    DMTDeatilRepo.save(DMTDetails);
			    DMTHistoryRepo.save(DMTHistory);
			}
			
			return ResponseEntity.ok(finalResponse);
			
		} catch(Exception e) {
			log.error("DMT Transaction Exception", e);
			error.setCode(String.valueOf(statusCode));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}

	//Transaction Status Check 
	@Override
	public ResponseEntity<?> tranxStatusCheck(DmtCommonrequestDto input, HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DmtCommonResponseDto finalResponse = new DmtCommonResponseDto();
		Integer statusCode = null;
		String idempotentKey = String.valueOf(System.currentTimeMillis());
		
		try {
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
			//Request
			TransactionDMT trans = new TransactionDMT();
			trans.setIdempotentKey(idempotentKey);
			
			Method meth = new Method();
			meth.setType(425);
			meth.setSubType(612);
			trans.setMethod(meth);			
			
			log.info("JSON Request for DMT Transaction Status Check :: {}", mapper.writeValueAsString(trans));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<TransactionDMT> entity = new HttpEntity<>(trans, header);
			
			try {
				
				response = rest.exchange(StatusCheckURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for DMT Transaction Status Check :: {}", response.getBody());

				if (response != null) {
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

			} catch (ResourceAccessException ex) {
				statusCode = 408;
				responseBody = "Timeout: " + ex.getMessage();
				log.error("API timeout", ex);

			} catch (Exception ex) {
				statusCode = 500;
				responseBody = "Unexpected error: " + ex.getMessage();
				log.error("API failure", ex);
			}
			
			return ResponseEntity.ok(response.getBody());
			
		} catch(Exception e) {
			log.error("DMT Transaction Status Check Exception", e);
			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}
	
	//Transaction History
	@Override
	public ResponseEntity<?> tranxHistory(DmtCommonrequestDto input, HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DmtCommonResponseDto finalResponse = new DmtCommonResponseDto();
		Integer statusCode = null;
		
		try {
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
//			String URL = "https://sit-apig.test.jiobank.in:9402/jpb/exp/v1/remittance/dmr/history/";			
//			String finalURL = URL + input.getMobile();
//			log.info("Final URL for DMT Transaction History :: {}", finalURL);
//			
//			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
//					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());
//
//			HttpEntity<String> entity = new HttpEntity<>(header);
			
//			{"mobile":"8318682508","agentId":"A985879971","remitterMobileNo":""}
			
			//Request
			Map<String, Object> request = new LinkedHashMap<>();
			request.put("mobile", input.getMobile());
			request.put("remitterMobileNo", "");
			request.put("agentId", "A985879971");
			
			log.info("JSON Request for DMT Transaction Histrory :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, header);
			
			try {
				response = rest.exchange(DMTTransactionHistoryURL, HttpMethod.GET, entity, String.class);
				log.info("JSON Raw Response for DMT Transaction History :: {}", response.getBody());

				if (response != null) {
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

			} catch (ResourceAccessException ex) {
				statusCode = 408;
				responseBody = "Timeout: " + ex.getMessage();
				log.error("API timeout", ex);

			} catch (Exception ex) {
				statusCode = 500;
				responseBody = "Unexpected error: " + ex.getMessage();
				log.error("API failure", ex);
			}
			
			return ResponseEntity.ok(response.getBody());
			
		} catch(Exception e) {
			log.error("DMT Transaction History Exception", e);
			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}
	
	//Customer Limit Check
	@Override
	public ResponseEntity<?> customerLimit(DmtCommonrequestDto input, HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null, remitterdID = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Integer statusCode = null;
		DMTCustomerMasterEntity masterEntity = new DMTCustomerMasterEntity();
		
		try {
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
			Optional<DMTCustomerMasterEntity> customerRecord = customerMasterRepo.findByCustomerMobileNoAndDmtPartnerId(input.getMobile(), partnerId);
			if(customerRecord.isPresent()) {
				masterEntity = customerRecord.get();
				
				if(masterEntity.getCustomerId() == null || masterEntity.getCustomerId().isBlank()) {
					responseBody = """
							{
							    "yearlyLimit": "0",
							    "yearlyCount": "0",
							    "monthlyCount": "0",
							    "dailyLimit": "0",
							    "monthlyLimit": "0",
							    "dailyCount": "0"
							}
							""";
					log.info("The Customer-ID has not been generated yet in JPB");
					return ResponseEntity.ok(responseBody);
				} else {
					remitterdID = masterEntity.getCustomerId();
				}
			}
			
			Date date = new Date();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String formattedDate = sdf.format(date);
			
			//Request
			Map<String, Object> request = new LinkedHashMap<>();
			request.put("productCode", "DMR");
			request.put("subProductCode", "C2A");
			request.put("customerId", remitterdID); //will get from verify OTP
			request.put("customerType","REMITTER");
			request.put("transactionDate", formattedDate);	
			
			log.info("JSON Request for DMT Customer Limit Check :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, header);
			
			try {
				
				response = rest.exchange(CustomerLimitURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for DMT Customer Limit Check :: {}", response.getBody());				

				if (response != null) {
					responseBody = response.getBody();
					statusCode = response.getStatusCode().value();
					JsonNode node = mapper.readTree(responseBody);
					finalResponse.setYearlyLimit(node.get("yearlyLimit").asText());
					finalResponse.setYearlyCount(node.get("yearlyCount").asText());
					finalResponse.setMonthlyCount(node.get("monthlyCount").asText());
					finalResponse.setDailyLimit(node.get("dailyLimit").asText());
					finalResponse.setMonthlyLimit(node.get("monthlyLimit").asText());
					finalResponse.setDailyCount(node.get("dailyCount").asText());
				}else {
					responseBody = """
							{
							    "yearlyLimit": "0",
							    "yearlyCount": "0",
							    "monthlyCount": "0",
							    "dailyLimit": "0",
							    "monthlyLimit": "0",
							    "dailyCount": "0"
							}
							""";
					JsonNode node = mapper.readTree(responseBody);
					finalResponse.setYearlyLimit(node.get("yearlyLimit").asText());
					finalResponse.setYearlyCount(node.get("yearlyCount").asText());
					finalResponse.setMonthlyCount(node.get("monthlyCount").asText());
					finalResponse.setDailyLimit(node.get("dailyLimit").asText());
					finalResponse.setMonthlyLimit(node.get("monthlyLimit").asText());
					finalResponse.setDailyCount(node.get("dailyCount").asText());
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

			} catch (ResourceAccessException ex) {
				statusCode = 408;
				responseBody = "Timeout: " + ex.getMessage();
				log.error("API timeout", ex);

			} catch (Exception ex) {
				statusCode = 500;
				responseBody = "Unexpected error: " + ex.getMessage();
				log.error("API failure", ex);
			}

			return ResponseEntity.ok(finalResponse);
						
		} catch(Exception e) {
			log.error("DMT Customer Limit Check Exception", e);
			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}
	
	//Beneficiary Validation
	@Override
	@Transactional
	public ResponseEntity<?> beneValidation(DmtCommonrequestDto input, HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		ErrorDetails error = new ErrorDetails();
		String responseBody = null;
		ResponseEntity<String> response = null;
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Integer statusCode = null, dmtCustomerId = null;
		String idempotentKey = String.valueOf(System.currentTimeMillis());
		String agentId = null, customerId = null, stateCode = null, agentPincode = null;
		DMTCustomerMasterEntity masterEntity = new DMTCustomerMasterEntity();
		DMTAddBeneficiaryEntity beneficiary = new DMTAddBeneficiaryEntity();
		boolean paymentStatus;
		try {
			
			// Money Debit----------
			DebitTransactionEntity debit = debitRepo.debitTransaction
					(idempotentKey, //unique transaction ID
					"Bene Validation For JPB DMT", //Remarks
					serviceID,
					subServiceID,
					subSubServiceID, //Sub Sub Service ID 1485-100, 1486-400 
					subSubSubServiceID,
					input.getVkid(),
					walletID,
					3.00, //Amount
					input.getVkid(), //BCID
					idempotentKey, //RRN No
					transactionAuthenticationType,
					transactionType
					);
					
			log.info("Debit Transaction Details :: {}", debit.toString());
			paymentStatus = "Y".equalsIgnoreCase(debit.getUpdateStatus()) ? true : false;
			
			if(paymentStatus) {
					log.info("Payment Successful---->");
			} else {
					log.info("Payment Pending---->");
			}
			
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}
			
			Optional<AgentMasterEntity> agentEntity = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid());
			if (agentEntity.isPresent()) {
				AgentMasterEntity agent = agentEntity.get();
				agentId = agent.getJioAgentId();
				stateCode = agent.getStateCode();
				agentPincode = agent.getPincode();
				log.info("Agent Master Details for the VKID :: {}, {}", input.getVkid(), agent.toString());
			}
			
			Optional<DMTCustomerMasterEntity> masterEntityRecord = customerMasterRepo.findByCustomerMobileNoAndDmtPartnerId(input.getSenderMobileNo(), partnerId);
			
			if(masterEntityRecord.isPresent()) {
				masterEntity = masterEntityRecord.get();
				customerId = masterEntity.getCustomerId();
				dmtCustomerId = masterEntity.getDmtCustomerId();
				log.info("Customer/Remitter-ID :: {}", customerId);
				log.info("Column ID of Customer ID :: {}", dmtCustomerId);
			}
			
			//Request
			DMTBeneValidationRequestDTO request = new DMTBeneValidationRequestDTO();
			
			//Transaction
			TransactionDMT trans = new TransactionDMT();
			trans.setIdempotentKey(idempotentKey);
			trans.setCurrency(356);
			trans.setInvoice(idempotentKey);
			trans.setMode(2);
			trans.setCaptureMethod(1);
			trans.setLivemode("true");
			trans.setApplication(Integer.parseInt(channelID));
			trans.setInitiatingEntityTimestamp(Instant.now());
			
			//Method
			Method method = new Method();
			method.setType(309);
			method.setSubType(631);
			trans.setMethod(method);
			
			//MetaData
			Metadata meta = new Metadata();
			//Agent
			Agent agent = new Agent();
			agent.setId("10000000000000005321"); // will check with the loginId of Agent
			AgentUser agUser = new AgentUser();
			agUser.setId(agentId);
			agent.setAgentUser(agUser);
			
			//Organization
			OrganizationDTO org = new OrganizationDTO();
			org.setId(channelID);
			agent.setOrganization(org);
			
			//Initiating Entity
			InitiatingEntity ini = new InitiatingEntity();
			ini.setEntityId(Integer.parseInt(channelID));
			trans.setInitiatingEntity(ini);
			
			//Address
			AddressDTO add = new AddressDTO();
			add.setStateCode(stateCode);//change according to the code
			add.setPincode(agentPincode);
			agent.setAddress(add);
			
			meta.setAgent(agent);
			trans.setMetadata(meta);
			request.setTransaction(trans);
			
			//Amount
			Amount amt = new Amount();
			Charges charges = new Charges();
			charges.setValue((double) 3);
			charges.setIgst(0.46);
			charges.setCgst((double) 0);
			charges.setSgst((double) 0);
			charges.setUgst((double) 0);
			charges.setServiceCharge(2.54);
			charges.setTds((double) 0);
			charges.setTcs(null);
			charges.setType(null);
			amt.setCharges(Collections.singletonList(charges));
			amt.setGrossAmount("3.0"); //Hardcoded values
			amt.setNetAmount("3.0"); //Hardcoded values
			request.setAmount(amt);
			
			//Payer
			PayerDto payer = new PayerDto();
			payer.setUserId(customerId);
			payer.setType(13);
			Mobile mob = new Mobile();
			mob.setCountryCode(countryCode);
			mob.setNumber(input.getSenderMobileNo());
			payer.setMobile(mob);
			request.setPayer(payer);
			
			//Payee
			PayerDto payee = new PayerDto();
			payee.setAccountNumber(input.getAccNo());
			payee.setBankIfsc(input.getBankIFSC());
			request.setPayee(payee);
			
			//Secure
			SecureDTO secure = new SecureDTO();
			secure.setEncryptionKey(encrypt.generateRandomString(16));
			
			//Device
			DMTDeviceInfoDTO device = new DMTDeviceInfoDTO();
			DMTDeviceSource source = mapper.readValue(util.getDMTDeviceInfoJson(httpRequest), DMTDeviceSource.class);
			device.setSource(source);
			device.setPeripheral(""); //encrypted key 
			secure.setDeviceInfo(device);
			request.setSecure(secure);
			
			log.info("JSON Request for DMT Beneficiary Check :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<DMTBeneValidationRequestDTO> entity = new HttpEntity<>(request, header);
			
			try {
				response = rest.exchange(BeneValidateURL, HttpMethod.POST, entity, String.class);
				log.info("JSON Raw Response for DMT Beneficiary Validation :: {}", response.getBody());

				if (response != null) {
					
//					if(true) {
//						responseBody = """
//								{
//"responseCode": "00",
//"responseMessage": "SUCCESS",
//"responseData": {
//"rrn": "433115512505",
//"transactionId": "10672433157085605000",
//"originalId": "10012433155653258000",
//"transactionNetAmount": 2.54,
//"transactionGrossAmount": 3.0,
//"transactionTime": "Tue Nov 26 15:51:26 IST 2024",
//"beneficiaryName": "NPCI BENI",
//"beneficiaryAccountNumber": "123456041",
//"beneficiaryBankName": "HDFC0000077"
//}
//}
//								""";
//						statusCode = 200;
					
					
//					responseBody = response.getBody();
//					statusCode = response.getStatusCode().value();
				}
			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error: {}, body: {}", statusCode, responseBody);

				finalResponse.setError(error);

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
					
					finalResponse = mapper.readValue(responseBody, DMTApiCommonResponseDTO.class);
					
				} catch (Exception e) {
					error.setCode("500");
					error.setMessage("Response parsing failed");
					finalResponse.setError(error);
					finalResponse.setStatus("FAILURE");
				}
			} else {

				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setStatus("FAILURE");
				finalResponse.setError(error);
			}
			
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getResponseMessage()) && "00".equalsIgnoreCase(finalResponse.getResponseCode())) {
				
				//finalResponse.setMessage("Beneficiary verified successfully.");
				
				Optional<DMTAddBeneficiaryEntity> addBeneRecord = addBeneRepo.findByDmtCustomerIdAndDmtPartnerIdAndAccountNo
						(dmtCustomerId, partnerId, input.getAccNo());
				
				if(addBeneRecord.isPresent()) {
					beneficiary = addBeneRecord.get();
					
					beneficiary.setStatus(1);
					beneficiary.setIsVerified(1);
					beneficiary.setRrnId(finalResponse.getResponseData().getRrn());
					beneficiary.setTid(finalResponse.getResponseData().getTransactionId());
					beneficiary.setGrossAmount(paymentStatus ? 3.00 : null);
					beneficiary.setBankCustomerName(finalResponse.getResponseData().getBeneficiaryName());
					addBeneRepo.save(beneficiary);
					
					finalResponse.setStatus("SUCCESS");
				    finalResponse.setMessage("Beneficiary verified successfully");
					
					log.info("Beneficiary verified successfully.");
					
				} else {
					beneficiary.setDmtCustomerId(dmtCustomerId.toString());
					beneficiary.setDmtPartnerId(partnerId);
					beneficiary.setAccountNo(input.getAccNo());
					beneficiary.setIfsc(input.getBankIFSC());
					beneficiary.setAccountType(0);
					beneficiary.setVkid(input.getVkid());
					beneficiary.setRecipientName(input.getReceiverName());
					beneficiary.setRecipientMobileNo(input.getReceiverMobileNo());
					beneficiary.setDescription("JPB");
					beneficiary.setStatus(1);
					beneficiary.setIsVerified(1);
					beneficiary.setStartDate(LocalDate.now());
					beneficiary.setRrnId(finalResponse.getResponseData().getRrn());
					beneficiary.setTid(finalResponse.getResponseData().getTransactionId());
					beneficiary.setGrossAmount(paymentStatus ? 3.00 : null);
					beneficiary.setSessionId(dmtCustomerId+input.getAccNo()+input.getBankIFSC());
					beneficiary.setBankCustomerName(finalResponse.getResponseData().getBeneficiaryName());
					
					beneficiary = addBeneRepo.save(beneficiary);

				    log.info("Beneficiary created & verified successfully.");
				    finalResponse.setStatus("SUCCESS");
			        finalResponse.setMessage("Beneficiary created & verified successfully");
				}
			} else if ("FAILURE".equalsIgnoreCase(finalResponse.getResponseMessage())) {
				
				beneficiary.setDmtCustomerId(dmtCustomerId.toString());
				beneficiary.setDmtPartnerId(partnerId);
				beneficiary.setAccountNo(input.getAccNo());
				beneficiary.setIfsc(input.getBankIFSC());
				beneficiary.setAccountType(0);
				beneficiary.setVkid(input.getVkid());
				beneficiary.setRecipientName(input.getReceiverName());
				beneficiary.setRecipientMobileNo(input.getReceiverMobileNo());
				beneficiary.setDescription("JPB");
				beneficiary.setStatus(0);
				beneficiary.setIsVerified(0);
				beneficiary.setStartDate(LocalDate.now());
				beneficiary.setGrossAmount(paymentStatus ? 3.00 : null);

			    beneficiary = addBeneRepo.save(beneficiary);
			    
			    log.info("Beneficiary created, but not verified.");
			    finalResponse.setStatus("FAILURE");
		        finalResponse.setMessage("Beneficiary created, but not verified");
			} else {
				finalResponse.setMessage(finalResponse.getResponseMessage());
				finalResponse.setStatus("FAILURE");
			}
			
			return ResponseEntity.ok(finalResponse);
			
		} catch(Exception e) {
			log.error("DMT Customer Limit Check Exception", e);
			error.setCode("200");
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}
	
	//Add Beneficiary
	@Transactional
	@Override
	public ResponseEntity<?> addBene(DmtCommonrequestDto input, HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		ErrorDetails error = new ErrorDetails();
		DMTCustomerMasterEntity masterEntity = new DMTCustomerMasterEntity();
		DMTAddBeneficiaryEntity beneficiary = new DMTAddBeneficiaryEntity();
		String customerID = null;
		Integer dmtCustomerID = null;
		
		try {
			
			log.info("Add Beneficiary JSON Request from Customer :: {}", mapper.writeValueAsString(input));;
			
			if("N".equalsIgnoreCase(input.getBeneAcctVerifyFlag())) {
				//DMT Customer ID
				Optional<DMTCustomerMasterEntity> masterEntityRecord = customerMasterRepo.findByCustomerMobileNoAndDmtPartnerId(input.getSenderMobileNo(), partnerId);
				
				if(masterEntityRecord.isPresent()) {
					masterEntity = masterEntityRecord.get();
					customerID = masterEntity.getCustomerId(); // jio
					dmtCustomerID = masterEntity.getDmtCustomerId();//Primary key of DMT customer Master
					log.info("customerID :: {}, DMT Customer ID :: {}", customerID, dmtCustomerID);
				} else {
					finalResponse.setStatus("Failure");
			        finalResponse.setMessage("Please On-Board Customer First");
			        return ResponseEntity.ok(finalResponse);
				}
				
				if(customerID != null && !"".equalsIgnoreCase(customerID)) {
					Optional<DMTAddBeneficiaryEntity> addBeneRecord = addBeneRepo.findByDmtCustomerIdAndDmtPartnerIdAndAccountNo
							(dmtCustomerID, partnerId, input.getAccNo());
					
					if (addBeneRecord.isPresent()) {
					    beneficiary = addBeneRecord.get();
					    log.info("Beneficiary already exists with id: {}", beneficiary.getDmtRecipientId());
				        finalResponse.setStatus("FAILURE");
				        finalResponse.setMessage("Beneficiary already exists with id:" + beneficiary.getDmtRecipientId());
					} else {

//						beneficiary.setDmtCustomerId(customerID);
						beneficiary.setDmtCustomerId(dmtCustomerID.toString());
						beneficiary.setDmtPartnerId(partnerId);
						beneficiary.setAccountNo(input.getAccNo());
						beneficiary.setIfsc(input.getBankIFSC());
						beneficiary.setAccountType(0);
						beneficiary.setVkid(input.getVkid());
						beneficiary.setRecipientName(input.getReceiverName());
						beneficiary.setRecipientMobileNo(input.getReceiverMobileNo());
						beneficiary.setDescription("JPB");
						beneficiary.setStatus(0);
						beneficiary.setIsVerified(0);
						beneficiary.setStartDate(LocalDate.now());
						beneficiary.setSessionId(dmtCustomerID+input.getAccNo()+input.getBankIFSC());
//						Optional.ofNullable(input.getBankName()).ifPresent(bankName - {
//							beneficiary.set
//						});	
						
						beneficiary = addBeneRepo.save(beneficiary);

					    log.info("Beneficiary created successfully.");
					    finalResponse.setStatus("Success");
				        finalResponse.setMessage("Beneficiary created successfully");
					}
				} else {
					finalResponse.setStatus("Failure");
			        finalResponse.setMessage("Beneficiary not added");
				}
			} else if("Y".equalsIgnoreCase(input.getBeneAcctVerifyFlag())) {
				return beneValidation(input, httpRequest);
			}
			
			return ResponseEntity.ok(finalResponse);
			
		} catch (HttpClientErrorException e) {

			log.error("DMT Add Beneficiary Exception", e);

			error.setCode("200");
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}
	
	//Customer Details for mobile team
	@Override
	@Transactional
	public ResponseEntity<?> custDetails(DmtCommonrequestDto input, HttpServletRequest httpRequest) {
		
		ErrorDetails error = new ErrorDetails();
		ObjectMapper mapper = new ObjectMapper();
		DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
		Map<String, Object> mobResponse = new LinkedHashMap<>();
		
		try {
			
			//UserExists
			ResponseEntity<?>userExistsResponse = checkmobileNo(input, httpRequest);
			DMTApiCommonResponseDTO mobileResult = (DMTApiCommonResponseDTO) userExistsResponse.getBody();
			
			
			if("SUCCESS".equalsIgnoreCase(mobileResult.getStatus())){	
				
				List<BeneficiaryListEntity> beneListRecords = beneListRepo.beneficiaryList(input.getMobile());
				
				//Limit Check
				ResponseEntity<?> limitCheckResponse = customerLimit(input, httpRequest);
				DMTApiCommonResponseDTO limitCheckResult = (DMTApiCommonResponseDTO) limitCheckResponse.getBody();
				
				mobResponse.put("customerLimit", limitCheckResult);	
				mobResponse.put("status", "SUCCESS");
				mobResponse.put("message", "Customer Details fetched successfully");
				mobResponse.put("customerName", mobileResult.getData().getFirstName() + " " 
						+ mobileResult.getData().getLastName());
				mobResponse.put("customerNumber", input.getMobile());
				mobResponse.put("beneficiaries", beneListRecords);
				mobResponse.put("ekycCharges", 10.00);
				mobResponse.put("beneVerificationCharges", 3.00);
			
			} else {
				mobResponse.put("customerLimit", new LinkedHashMap<>());	
				mobResponse.put("status", "FAILURE");
				mobResponse.put("message", "Customer should be registered first");
				mobResponse.put("customerName", null);
				mobResponse.put("customerNumber", input.getMobile());
				mobResponse.put("beneficiaries", new ArrayList<String>());
				mobResponse.put("ekycCharges", 10.00);
				mobResponse.put("beneVerificationCharges", 3.00);
			}	
			
			return ResponseEntity.ok(mobResponse);
			
		} catch (HttpClientErrorException e) {

			log.error("DMT Customer Details Exception", e);
			error.setCode("200");
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}		
	}

	//Amount Calulation According to service
	@Override
	public ResponseEntity<?> amtCalculation(DmtCommonrequestDto request, HttpServletRequest httpRequest) {

		String sql = "{call VLPaymentGateway.GetAmountToBeDebited(?, ?, ?, ?, ?, ?, ?)}";
	    
	    DMTApiCommonResponseDTO finalResponse = new DMTApiCommonResponseDTO();
	    try {

	        List<DMTApiCommonResponseDTO> results = jdbc.query(
	                sql,
	                ps -> {
	                    ps.setString(1, "13");
	                    ps.setString(2, "143");
	                    ps.setString(3, "171");
	                    ps.setString(4, "65");
	                    ps.setString(5, request.getNetAmount());
	                    ps.setString(6, "");
	                    ps.setString(7, request.getVkid());
	                },
	                (rs, rowNum) -> {

	                	DMTApiCommonResponseDTO response = new DMTApiCommonResponseDTO();
	                    response.setResponseType(rs.getString("ResponseType"));

	                    Double transactionAmount = rs.getDouble("transactionAmount");
	                    Double amountToBeDebited = rs.getDouble("AmountToBeDeductedAfterGST");

	                    response.setTransactionAmount(transactionAmount);
	                    response.setAmountToBeDebited(amountToBeDebited);
	                    response.setServiceCharge(amountToBeDebited - transactionAmount);
	                    response.setStatus("SUCCESS");
	                    response.setMessage("Transaction Details fetched successfully");	                    
	                    return response;
	                });

	        if (results.isEmpty()) {
	        	finalResponse.setStatus("FAILURE");
	        	finalResponse.setMessage("Service Charges are yet underway");
	            return ResponseEntity.ok(finalResponse);
	        }

	        DMTApiCommonResponseDTO response = results.get(0);

	        if (!"OKAY".equalsIgnoreCase(response.getResponseType())) {
	        	finalResponse.setStatus("FAILURE");
	        	finalResponse.setMessage("Transaction Charges failed");
	            return ResponseEntity.ok(finalResponse);
	        }

	        return ResponseEntity.ok(response);

	    } catch (Exception ex) {
	        log.error("Error while calculating amount to be debited", ex);
	        return ResponseEntity.internalServerError()
	                .body("Something went wrong. Please try again.");
	    }
	}
	
}
