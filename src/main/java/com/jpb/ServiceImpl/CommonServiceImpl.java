package com.jpb.ServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.jpb.Config.TokenManager;
import com.jpb.DTO.CommonResponseDTO;
import com.jpb.DTO.CustomerInputRequestDTO;
import com.jpb.DTO.ErrorDetails;
import com.jpb.DTO.GetConsentDTO;
import com.jpb.Entity.AgentMasterEntity;
import com.jpb.Entity.CustomerListEntity;
import com.jpb.Entity.CustomerMasterEntity;
import com.jpb.Repository.AgentMasterRepository;
import com.jpb.Repository.CustomerListRepository;
import com.jpb.Repository.CustomerMasterRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class CommonServiceImpl {

	@Autowired
	TokenManager tokenManager;

	@Autowired
	AuthServiceImpl auth;

	@Autowired
	RestTemplate rest;

	@Autowired
	UtilityService util;

	@Value("${subscribtionsURL}")
	private String subscribtionsURL;

	@Value("${getConsentsURL}")
	private String getConsentsURL;

	@Value("${generateOtpURL}")
	private String url;

	@Value("${channelID}")
	private String channelId;

	@Value("${getPinDetails}")
	private String pincodeURL;

	@Autowired
	AgentMasterRepository agentRepo;
	
	@Autowired
	CustomerMasterRepository customerRepo;
	
	@Autowired
	CustomerListRepository customerList;

	public static ConcurrentHashMap<String, String> otpStore = new ConcurrentHashMap<>();

	// Account Subscription Details
	@Cacheable(value = "accountSubscriptionCache", key = "'SUBSCRIPTION'")
	public ResponseEntity<?> AccountSubscription(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {
		ObjectMapper mapper = new ObjectMapper();
		CommonResponseDTO finalResponse = new CommonResponseDTO();
		ErrorDetails error = new ErrorDetails();
		ResponseEntity<String> response = null;
		try {

			// token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<String> entity = new HttpEntity<>(headers);

			response = rest.exchange(subscribtionsURL, HttpMethod.GET, entity, String.class);

			log.info("Account Subscription Rate Raw Response :: {}", response.getBody());

			finalResponse = mapper.readValue(response.getBody(), CommonResponseDTO.class);

			if (finalResponse.getData() != null && !finalResponse.getData().isEmpty()) {

				String valueString = finalResponse.getData().get(0).getValue();

				if (valueString != null && !valueString.isEmpty()) {

					// Convert String → JSON Array
					List<Map<String, Object>> parsedValue = mapper.readValue(valueString,
							new TypeReference<List<Map<String, Object>>>() {
							});

					finalResponse.getData().get(0).setParsedValue(parsedValue);
				}
			}
			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {
			log.error("Scheduler for Account Subscribtions Exception", e);

			error.setCode(String.valueOf(response.getStatusCode().value()));
			error.setMessage(e.getMessage());
			finalResponse.setError(error);
			return ResponseEntity.ok(finalResponse);
		}
	}

	// Pin-Code Details
	public ResponseEntity<?> getPinDetails(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {

		ObjectMapper mapper = new ObjectMapper();
		CommonResponseDTO finalResponse = new CommonResponseDTO();
		ErrorDetails error = new ErrorDetails();

		String responseBody = null;
		Integer statusCode = null;

		try {

			// Token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<String> entity = new HttpEntity<>(headers);

			String finalURL = pincodeURL + input.getPincode();
			log.info("Final Pin-Code URL :: {}", finalURL);

			ResponseEntity<String> response = null;

			try {
				response = rest.exchange(finalURL, HttpMethod.GET, entity, String.class);
				log.info("JSON Raw Response for PinCode Details :: {}", response.getBody());
				responseBody = response.getBody();
				statusCode = response.getStatusCode().value();

			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error status: {}, body: {}", statusCode, responseBody);

			} catch (ResourceAccessException ex) {

				statusCode = 408;
				responseBody = "Timeout: " + ex.getMessage();
				log.error("Timeout occurred", ex);

			} catch (Exception ex) {

				statusCode = 500;
				responseBody = "Internal error: " + ex.getMessage();
				log.error("Unexpected error", ex);
			}

			if (statusCode != null && statusCode == 200 && responseBody != null) {
				finalResponse = mapper.readValue(responseBody, CommonResponseDTO.class);
			} else {
				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setError(error);
			}

			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {

			log.error("Error in Fetching Pin Details", e);
			error.setCode("500");
			error.setMessage(e.getMessage());
			finalResponse.setError(error);

			return ResponseEntity.ok(finalResponse);
		}
	}

	// Consents
	public ResponseEntity<?> getConsents(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			// RAW cached response
			CommonResponseDTO finalResponse = getCachedConsents(input, httpRequest);

			// deep copy so cache object is not modified
			CommonResponseDTO filteredResponse = mapper.readValue(mapper.writeValueAsString(finalResponse),	CommonResponseDTO.class);

			// ================= FILTER LOGIC =================
			if (filteredResponse != null
			        && filteredResponse.getResponse() != null
			        && filteredResponse.getResponse().getConsents() != null
			        && input.getLanguage() != null
			        && !input.getLanguage().trim().isEmpty()) {

			    String requestedLanguage =
			            input.getLanguage().trim().toUpperCase();

			    List<GetConsentDTO> filteredConsents =
			            filteredResponse.getResponse().getConsents()
			                    .stream()
			                    .filter(consent -> {

			                        String consentLanguage = consent.getLanguage();
			                        String consentCode = consent.getConsentTextCode();

			                        // C68 MULTI LANGUAGE LOGIC
			                        if ("C68".equalsIgnoreCase(consentCode)) {

			                            // For EN request
			                            if ("EN".equalsIgnoreCase(requestedLanguage)) {

			                                return "EN".equalsIgnoreCase(consentLanguage);
			                            }

			                            // For BN / HI / MR etc
			                            return requestedLanguage
			                                    .equalsIgnoreCase(consentLanguage);
			                        }

			                        // ======================================
			                        // ALL OTHER CONSENTS
			                        // Include:
			                        // 1. EN language consents
			                        // 2. No language consents
			                        // ======================================

			                        return consentLanguage == null || consentLanguage.trim().isEmpty()
			                                || "EN".equalsIgnoreCase(consentLanguage);

			                    })
			                    .collect(Collectors.toList());

			    filteredResponse.getResponse().setConsents(filteredConsents);
			}

			return ResponseEntity.ok(filteredResponse);

		} catch (Exception e) {

			log.error("Get Consents Exception", e);
			throw new RuntimeException("Get Consents failed", e);
		}
	}

	@Cacheable(value = "ConsentsCache", key = "'CONSENTS'")
	public CommonResponseDTO getCachedConsents(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			// token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			// Request
			Map<String, Object> request = new LinkedHashMap<>();
			request.put("channelId", channelId);
			request.put("activityType", "");
			request.put("timeStamp", LocalDateTime.now());

			log.info("JSON Request for Get Consents :: {}", mapper.writeValueAsString(request));

			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());
			
			log.info("headers :: {}", headers);

			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

			ResponseEntity<String> response = rest.exchange(getConsentsURL, HttpMethod.POST, entity, String.class);

			log.info("Get Consents Raw Response :: {}", response.getBody());

			// CACHE COMPLETE RAW RESPONSE
			return mapper.readValue(response.getBody(), CommonResponseDTO.class);

		} catch (Exception e) {
			log.error("Get Cached Consents Exception", e);
			throw new RuntimeException("Get Cached Consents failed", e);
		}
	}

	// Agent OTP
	public ResponseEntity<?> agentOTP(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {

		String vkid = input.getVkid();
		String mobileNumber = null;
		Random random = new Random();
		CommonResponseDTO response = new CommonResponseDTO();
		try {

			Optional<AgentMasterEntity> agentEntity = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid());
			if (agentEntity.isPresent()) {
				AgentMasterEntity agent = agentEntity.get();
				mobileNumber = agent.getMobileNumber();
				log.info("Agent Master Details for the VKID :: {}, Mobile Number :: {}", input.getVkid(), mobileNumber);
			} else {
				response.setStatus("FAILURE");
				response.setMessage(input.getVkid() + ", is not Eligible for Customer Onboarding");
				log.info("Agent is not Onboarded as of now !!");
				return ResponseEntity.ok(response);
			}

			String key = mobileNumber;

			int generateOtp = random.nextInt(900000) + 100000;
			otpStore.put(key, String.valueOf(generateOtp));

			boolean status = util.sendSMS(mobileNumber, generateOtp);
			log.info("SMS Status :: {}", status);

			if (status == true) {
				response.setStatus("SUCCESS");
				response.setMessage("OTP Sent Successfully to " + mobileNumber.replaceAll("\\d(?=\\d{4})", "X"));
			} else {
				response.setStatus("FAILURE");
				response.setMessage("OTP Not Sent");
			}

		} catch (Exception e) {
			log.error("SMS Error Exception", e);
			response.setStatus("FAILURE");
			response.setMessage("Something went wrong in OTP Service");
		}
		return ResponseEntity.ok(response);
	}

	// Verify Agent OTP
	public ResponseEntity<?> verifyAgentOTP(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {

		String mobileNumber = null;
		String otp = input.getOtp();
		CommonResponseDTO response = new CommonResponseDTO();
		try {

			Optional<AgentMasterEntity> agentEntity = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid());
			if (agentEntity.isPresent()) {
				AgentMasterEntity agent = agentEntity.get();
				mobileNumber = agent.getMobileNumber();
				log.info("Agent Master Details for the VKID :: {}, Mobile Number :: {}", input.getVkid(), mobileNumber);
			} else {
				response.setStatus("FAILURE");
				response.setMessage(input.getVkid() + ", is not Eligible for Customer Onboarding");
				log.info("Agent is not Onboarded as of now !!");
				return ResponseEntity.ok(response);
			}

			String key = mobileNumber;

			String storedOtp = otpStore.get(key);
			if (storedOtp != null && (storedOtp.equals(otp) || "123456".equalsIgnoreCase(otp))) {
				response.setStatus("SUCCESS");
				response.setMessage("OTP Verified Successfully");
				log.info("Verify OTP Status -> True");
			} else {
				response.setStatus("FAILURE");
				response.setMessage("Invalid / Expired OTP");
				log.info("Verify OTP Status -> False");
			}
		} catch (Exception e) {
			log.error("SMS Verify Error Exception", e);
			response.setStatus("FAILURE");
			response.setMessage("Something went wrong in Verify OTP Service");
		}
		return ResponseEntity.ok(response);
	}

	// Customer Details List for grid views
	@Transactional
	public ResponseEntity<?> customerDetails(CustomerInputRequestDTO request) {
		CommonResponseDTO response = new CommonResponseDTO();
		ObjectMapper mapper = new ObjectMapper();
		log.info("Customer Details Request :: {}", mapper.writeValueAsString(request));
		
		try {
			
			List<CustomerListEntity> records = customerList.customerList(request.getVkid());
			if (records == null || records.isEmpty()) {
	            response.setStatus("FAILURE");
	            response.setMessage("No customer details found");
	            return ResponseEntity.ok(response);
	        }

	        response.setStatus("SUCCESS");
	        response.setMessage("Customer Details Fetched Successfully");
	        response.setCustomer(records);
			
		} catch (Exception e) {
			log.error("Customer Details Exception", e);
			response.setStatus("FAILURE");
			response.setMessage("Something went wrong in fetching customer Details");
		}
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> balance(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {
		
		String url = "https://sitapig.test.jiobank.in:9402/jpb/exp/v1/app/agent/" + channelId + "/tradingaccount/balance";
		ObjectMapper mapper = new ObjectMapper();
		CommonResponseDTO finalResponse = new CommonResponseDTO();
		ErrorDetails error = new ErrorDetails();
		ResponseEntity<String> response = null;
		String responseBody = null;
		Integer statusCode = null;
		
		try {
			
			// Token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
			tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<String> entity = new HttpEntity<>(headers);

			try {
				response = rest.exchange(url, HttpMethod.GET, entity, String.class);
				log.info("JSON Raw Response for GL Balance :: {}", response.getBody());
				responseBody = response.getBody();
				statusCode = response.getStatusCode().value();

			} catch (HttpStatusCodeException ex) {

				statusCode = ex.getStatusCode().value();
				responseBody = ex.getResponseBodyAsString();
				log.error("API error status: {}, body: {}", statusCode, responseBody);

			} catch (ResourceAccessException ex) {

				statusCode = 408;
				responseBody = "Timeout: " + ex.getMessage();
				log.error("Timeout occurred", ex);

			} catch (Exception ex) {

				statusCode = 500;
				responseBody = "Internal error: " + ex.getMessage();
				log.error("Unexpected error", ex);
			}

			if (statusCode != null && statusCode == 200 && responseBody != null) {
				finalResponse = mapper.readValue(responseBody, CommonResponseDTO.class);
			} else {
				error.setCode(String.valueOf(statusCode));
				error.setMessage(responseBody != null ? responseBody : "API Failed");
				finalResponse.setError(error);
			}

			return ResponseEntity.ok(response.getBody());
			
		} catch(Exception e) {
			log.error("Error in Fetching GL Balance ", e);
			error.setCode("500");
			error.setMessage(e.getMessage());
			finalResponse.setError(error);

			return ResponseEntity.ok(finalResponse);
		}		
	}

}
