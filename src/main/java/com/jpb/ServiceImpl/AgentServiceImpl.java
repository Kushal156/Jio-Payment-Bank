package com.jpb.ServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jpb.Config.TokenManager;
import com.jpb.DTO.AadhaarDTO;
import com.jpb.DTO.ActionDTO;
import com.jpb.DTO.AddressDTO;
import com.jpb.DTO.AgentEkycRequestDTO;
import com.jpb.DTO.AgentEkycResponseDTO;
import com.jpb.DTO.AgentInfoResponseDTO;
import com.jpb.DTO.AgentStatusResponseDTO;
import com.jpb.DTO.AuthenticateEkycDTO;
import com.jpb.DTO.ConsentDTO;
import com.jpb.DTO.ContactDetailsDTO;
import com.jpb.DTO.ErrorDetails;
import com.jpb.DTO.FinancialDetailsDTO;
import com.jpb.DTO.GeoLocationDTO;
import com.jpb.DTO.OrganizationDTO;
import com.jpb.DTO.PanUpdateRequestDTO;
import com.jpb.DTO.PanUpdateResponseDTO;
import com.jpb.DTO.PaymentInstrumentDTO;
import com.jpb.DTO.PersonDTO;
import com.jpb.DTO.PersonPanDTO;
import com.jpb.DTO.ProductDTO;
import com.jpb.DTO.RecallApplicationRequestDTO;
import com.jpb.DTO.RecallApplicationResponseDTO;
import com.jpb.DTO.SaveAgentInputDTO;
import com.jpb.DTO.SaveAgentResponseDTO;
import com.jpb.Entity.AgentMasterEntity;
import com.jpb.Entity.InsertSPAgentDetailsEntity;
import com.jpb.Repository.AgentMasterRepository;
import com.jpb.Repository.InsertSPAgentDetails;
import com.jpb.DTO.SaveAgentRequestDTO;
import com.jpb.Service.AgentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class AgentServiceImpl implements AgentService {

	@Value("${AppName}")
	private String appName;

	@Value("${channelID}")
	private String channelId;

	@Value("${saveAgentURL}")
	private String saveAgentUrl;
	
	@Value("${agentInfoURL}")
	private String agentInfoUrl;

	@Autowired
	RestTemplate rest;

	@Autowired
	TokenManager tokenManager;

	@Autowired
	AuthServiceImpl auth;

	@Autowired
	UtilityService util;
	
	@Autowired
	InsertSPAgentDetails insertRepo;
	
	@Autowired
	AgentMasterRepository agentRepo;
	
	@Autowired
	JdbcTemplate jdbc;

	// Save Agent
	@Override
	@Transactional
	public ResponseEntity<SaveAgentResponseDTO> saveAgentDetails(SaveAgentInputDTO input,
			HttpServletRequest httpRequest) {

		log.info("Save Agent Input from User :: {}", input.toString());

		SaveAgentRequestDTO request = new SaveAgentRequestDTO();
		ObjectMapper mapper = new ObjectMapper();

		// Static Values
		request.setApiVersion("1.0");
		request.setApplicationType("Partner");
		request.setApplicationSubType("Onboarding");
		request.setInitiatingEntityId(channelId);
		request.setApp("APIPARTNER");

		// External Ref (must be present)
		if (input.getExternalAppRefNumber() != null) {
			request.setExternalAppRefNumber(input.getExternalAppRefNumber());
		} else {
			request.setExternalAppRefNumber("REF" + System.currentTimeMillis());
		}
		
		log.info("ExternalRefNo :: {}", request.getExternalAppRefNumber());

		// Action
		ActionDTO action = new ActionDTO();
		action.setType("Creation");
		action.setSubType("Save");
		request.setAction(action);

		// Organization
		OrganizationDTO org = new OrganizationDTO();
		org.setId(channelId);
		request.setOrganization(org);

		// PERSON (ONLY ONCE)
		PersonDTO person = new PersonDTO();
		person.setPersonType("INDIVIDUAL");
		person.setExternalId(request.getExternalAppRefNumber());

		// Financial (FROM USER)
		FinancialDetailsDTO financial = new FinancialDetailsDTO();
		financial.setPanNumber(input.getPanNumber()); // USER INPUT
		person.setFinancialDetails(financial);

		// Address (FROM USER)
		AddressDTO address = new AddressDTO();
		address.setAddressType("WORK");
		address.setStreet(input.getStreet());
		address.setCity(input.getCity());
		address.setState(input.getState());
		address.setStateCode(input.getStateCode());
		address.setCountry("India"); // STATIC
		address.setPincode(input.getPincode());

		// Geo Location (MANDATORY)
		if (input.getLatitude() == null || input.getLongitude() == null) {
			throw new RuntimeException("Latitude/Longitude is required");
		}

		GeoLocationDTO geo = new GeoLocationDTO();
		geo.setLatitude(input.getLatitude());
		geo.setLongitude(input.getLongitude());
		address.setGeoLocation(geo);

		person.setAddress(Collections.singletonList(address));

		// Contact (FROM USER)
		List<ContactDetailsDTO> contacts = new ArrayList<>();

		// Mobile
		if (input.getMobileNumber() != null) {
			ContactDetailsDTO mobile = new ContactDetailsDTO();
			mobile.setType("Mobile");
			mobile.setCountryCode("+91"); // STATIC
			mobile.setMobileNumber(input.getMobileNumber());
			mobile.setStatus("PreVerified"); // STATIC
			contacts.add(mobile);
		}

		// Email
		if (input.getEmail() != null) {
			ContactDetailsDTO email = new ContactDetailsDTO();
			email.setType("Personal Email");
			email.setEmail(input.getEmail());
			contacts.add(email);
		}

		person.setContactDetails(contacts);

		// Set Person List
		request.setPersons(Collections.singletonList(person));

		// PRODUCT (STATIC)
		PaymentInstrumentDTO dmr = new PaymentInstrumentDTO();
		dmr.setId("DMR");
		dmr.setEnabled(true);

		PaymentInstrumentDTO aeps = new PaymentInstrumentDTO();
		aeps.setId("AEPS");
		aeps.setEnabled(true);

		ProductDTO product = new ProductDTO();
		product.setProductType("AGENT");
		product.setPaymentInstruments(Arrays.asList(dmr, aeps));

		request.setProducts(Collections.singletonList(product));

		log.info("Final Request Format :: {}", request.toString());
		String jsonRequest = mapper.writeValueAsString(request);
		log.info("Final JSON Reuqest for Save Agent :: {}", jsonRequest);

		try {
			// Token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

//			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
//					tokenManager.getAppIdentifierToken());
			
			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<SaveAgentRequestDTO> entity = new HttpEntity<>(request, headers);

			ResponseEntity<String> response = rest.exchange(saveAgentUrl, HttpMethod.POST, entity, String.class);

//			String response = "{\r\n" +
//		        "    \"externalAppRefNumber\": \"REF1775794143040\",\r\n" +
//		        "    \"applicationNumber\": \"7245REF177582231769288183\",\r\n" +
//		        "    \"status\": \"SUCCESS\",\r\n" +
//		        "    \"nextAction\": {\r\n" +
//		        "        \"type\": \"AADHAAR\",\r\n" +
//		        "        \"subType\": \"EKYC-BIOMETRIC\"\r\n" +
//		        "    },\r\n" +
//		        "    \"data\": {\r\n" +
//		        "        \"applicationNumber\": \"7245REF177582231769288183\"\r\n" +
//		        "    }\r\n" +
//		        "}";
//			SaveAgentResponseDTO finalResponse = mapper.readValue(response, SaveAgentResponseDTO.class);

			 SaveAgentResponseDTO finalResponse = mapper.readValue(response.getBody(), SaveAgentResponseDTO.class);
			 log.info("Save Agent Response :: {}", response.getBody());
			
			String errorMsg = (finalResponse.getError() == null || finalResponse.getError().getMessage() == null)
				    ? "No Error Msg"
				    : finalResponse.getError().getMessage();
			
			String nextActionType = Optional.ofNullable(finalResponse.getNextAction())
			        .map(na -> na.getType())
			        .orElse(null);

			String nextActionSubType = Optional.ofNullable(finalResponse.getNextAction())
			        .map(na -> na.getSubType())
			        .orElse(null);
			 
			InsertSPAgentDetailsEntity spResponse = insertRepo.insertAgentWithLog(

			        finalResponse.getApplicationNumber(),
			        request.getExternalAppRefNumber(),
			        request.getApiVersion(),
			        request.getInitiatingEntityId(),
			        channelId,
			        request.getExternalAppRefNumber(),
			        input.getPanNumber(),
			        input.getPanName(),
			        input.getDob(),     // ensure String
			        input.getAadharNum(),
			        input.getStreet(),
			        input.getCity(),
			        input.getState(),
			        input.getStateCode(),
			        input.getPincode(),
			        input.getLatitude(),
			        input.getLongitude(),
			        "Mobile",
			        input.getMobileNumber(),
			        input.getEmail(),
			        "AEPS, DMR",
			        true,
			        input.getVkid(),
			        "Save_Agent",
			        jsonRequest,
			        response.getBody(),
//			        response,
			        200,
			        request.getExternalAppRefNumber(),
			        finalResponse.getStatus(),
			        errorMsg,
			        nextActionType,
			        nextActionSubType,
			        
			        request.getApplicationType(),
			        request.getApplicationSubType(),
			        "Partner",
			        request.getAction().getType(),
			        request.getAction().getSubType()
			);

			 log.info("SP Response :: {}", spResponse.toString());
			 
			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {
			log.error("Save Agent Failed :: ", e);
			throw new RuntimeException("Save Agent API failed", e);
		}
	}

	// Agent EKYC
	@Override
	@Transactional
	public ResponseEntity<?> agentEkyc(String applicationNo, String aadharNo, String biometricDataBase64,
			HttpServletRequest httpRequest,  String externalRefNo, String vkid, String latitude, String longitude) {

		log.info("Application No :: {}, Aadhar No :: {}", applicationNo, aadharNo);
		log.info("Bio-Metric Data :: {}", biometricDataBase64.toString());
		log.info("EKYC latitude :: {}, longitude :: {}", latitude, longitude);
		
		String base64String = util.convertPidXmlToBase64Json(biometricDataBase64); 

		AgentEkycRequestDTO request = new AgentEkycRequestDTO();
		ObjectMapper mapper = new ObjectMapper();

		try {
			// Ensure Token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			// Request Build
			request.setApplicationNumber(applicationNo);
			request.setApiVersion("1.0");
			request.setApplicationType("Partner");
			request.setApplicationSubType("Onboarding");
			request.setInitiatingEntityId(channelId);
			request.setApp("APIPARTNER");

			// Action
			ActionDTO action = new ActionDTO();
			action.setType("AADHAAR");
			action.setSubType("EKYC-BIOMETRIC");
			request.setAction(action);

			// Aadhaar
			AadhaarDTO aadhaar = new AadhaarDTO();
			aadhaar.setType("UID");
			aadhaar.setValue(aadharNo);

			AuthenticateEkycDTO auth = new AuthenticateEkycDTO();
			auth.setAadhaar(aadhaar);
			auth.setValue(base64String);

			request.setAuthenticateList(Collections.singletonList(auth));

			// Consent
			ConsentDTO consent = new ConsentDTO();
			consent.setConsent(
					"I hereby provide my consent to Jio Payments Bank Limited to use my Aadhaar and biometric authentication for eKYC.");
			consent.setCode("B88");
			consent.setVersion("1");
			consent.setMethod("checkbox");

			request.setConsents(Collections.singletonList(consent));

			log.info("EKYC Request :: {}", request.toString());
			log.info("EKYC JSON Request :: {}", mapper.writeValueAsString(request));

			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), latitude, longitude);

			HttpEntity<AgentEkycRequestDTO> entity = new HttpEntity<>(request, headers);

			ResponseEntity<String> response = rest.exchange(saveAgentUrl, HttpMethod.POST, entity, String.class);
			
//			String response = """
//					{"applicationNumber":"7245REF177727030109479170","status":"SUCCESS","nextAction":{"type":"UPDATE","subType":"PAN_NAME_DOB"},"data":{"dobStatus":"N","panStatus":"E","apiSuccess":true,"nameStatus":"N","aadhaarSeedStatus":"Y"}}
//					""";
//			AgentEkycResponseDTO finalResponse = mapper.readValue(response, AgentEkycResponseDTO.class);
			
			log.info("EKYC Raw Response :: {}", response.getBody());
			AgentEkycResponseDTO finalResponse = mapper.readValue(response.getBody(), AgentEkycResponseDTO.class);

			String errorMsg = (finalResponse.getError() == null || finalResponse.getError().getMessage() == null)
				    ? "No Error Msg"
				    : finalResponse.getError().getMessage();
			
			Optional<AgentMasterEntity> optional = agentRepo.findByApplicationNumber(applicationNo);
			if (optional.isPresent()) {
			    AgentMasterEntity agent = optional.get();
			    agent.setStatus(finalResponse.getStatus());
			    agent.setActionType("AADHAAR");
			    agent.setActionSubType("EKYC-BIOMETRIC");
			    
			    ErrorDetails error = finalResponse.getError();
			    
			    if(error == null || error.getCode() == null) {
			    	Optional.ofNullable(finalResponse.getNextAction()).ifPresent(nextAction ->{
				    	agent.setNextActionType(nextAction.getType());
					    agent.setNextActionSubType(nextAction.getSubType());
				    });
			    } else if("1006".equalsIgnoreCase(finalResponse.getError().getCode())){
			    	agent.setNextActionType("GENERATE NEW");
				    agent.setNextActionSubType("EXIT");
			    } else if("Onboarding Data saved successfully.".equalsIgnoreCase(finalResponse.getMessage())) {
			    	agent.setNextActionType("AGENT ONBOARDED");
				    agent.setNextActionSubType("COMPLETE");
			    }    
			    agent.setUpdatedAt(LocalDateTime.now());
			    agentRepo.save(agent);
			    log.info("Details Updated Successfully for Application No :: {}", applicationNo);
			}
			
			String nextActionType = Optional.ofNullable(finalResponse.getNextAction())
			        .map(na -> na.getType())
			        .orElse(null);

			String nextActionSubType = Optional.ofNullable(finalResponse.getNextAction())
			        .map(na -> na.getSubType())
			        .orElse(null);
			
			InsertSPAgentDetailsEntity spResponse = insertRepo.insertAgentWithLog(
			        applicationNo,   
			        externalRefNo,
			        request.getApiVersion(),
			        request.getInitiatingEntityId(),
			        channelId,
			        externalRefNo,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        "Mobile",
			        null,
			        null,
			        "AEPS, DMR",
			        true,   // ✅ Boolean instead of "1"
			        vkid,
			        "Agent_EKYC",
			        mapper.writeValueAsString(request),
			        response.getBody(),
//			        response,
			        200,   // ✅ Integer instead of "200"
			        null,
			        finalResponse.getStatus(),
			        errorMsg,
			        nextActionType,
			        nextActionSubType,
			        
			        request.getApplicationType(),
			        request.getApplicationSubType(),
			        "Partner",
			        request.getAction().getType(),
			        request.getAction().getSubType()
			);
			
			log.info("SP Response :: {}", spResponse.toString());
			
			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {
			log.error("Agent EKYC Failed :: {}", e);
			throw new RuntimeException("Agent EKYC Failed ", e);
		}
	}

	// EKYC Status Check
	@Override
	public ResponseEntity<AgentStatusResponseDTO> agentStatus(String applicationNo, HttpServletRequest httpRequest, String latitude, String longitude) {

		ObjectMapper mapper = new ObjectMapper();
		log.info("Application No :: {}", applicationNo);
		log.info("EKYC Status Check latitude :: {}, longitude :: {}", latitude, longitude);
		try {

			// Token
			if (!tokenManager.isAccessTokenValid()) {
				log.info("Token expired → generating new token");
				auth.generateToken(httpRequest);
			}

			// URL Building
			String url = saveAgentUrl + "?applicationNumber=" + applicationNo;

			log.info("Final URL for Agent Status Check :: {}", url);

			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), latitude, longitude);

			HttpEntity<Void> entity = new HttpEntity<>(headers);

			ResponseEntity<String> response = rest.exchange(url, HttpMethod.GET, entity, String.class);
			
			log.info("Agent Status Raw Response :: {}", response.getBody());
			
			AgentStatusResponseDTO finalResponse = mapper.readValue(response.getBody(), AgentStatusResponseDTO.class);

			String errorMsg = (finalResponse.getError() == null || finalResponse.getError().getMessage() == null)
				    ? "No Error Msg"
				    : finalResponse.getError().getMessage();
			
			return ResponseEntity.ok(finalResponse);

		} catch (Exception e) {
			log.error("Agent Status Failed", e);
			throw new RuntimeException("Agent Status API failed", e);
		}
	}
	
	//Pan Update
	@Override
	@Transactional
	public ResponseEntity<?> panUpdate(String applicationNo, String panNumber, String panName, String dob,
	                                   HttpServletRequest httpRequest,  String externalRefNo, String vkid,
	                                   String latitude, String longitude) {

	    log.info("Application No :: {}, Pan No :: {}, Name on Pan :: {}, DOB :: {}",
	            applicationNo, panNumber, panName, dob);
	    log.info("Pan Update latitude :: {}, longitude :: {}", latitude, longitude);

	    ObjectMapper mapper = new ObjectMapper();

	    try {
	    	
	    	 DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	         DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

	         LocalDate parsedDob = LocalDate.parse(dob, inputFormatter);
	         String formattedDob = parsedDob.format(outputFormatter);

	         log.info("DOB (Formatted) :: {}", formattedDob);

	        //Validations
	        if (applicationNo == null || applicationNo.isEmpty()) {
	            throw new RuntimeException("Application Number is required");
	        }

	        if (panNumber == null || panNumber.length() != 10) {
	            throw new RuntimeException("Invalid PAN Number");
	        }

	        if (dob == null || dob.isEmpty()) {
	            throw new RuntimeException("DOB is required");
	        }

	        //Token
	        if (!tokenManager.isAccessTokenValid()) {
	            log.info("Token expired → generating new token");
	            auth.generateToken(httpRequest);
	        }

	        //Build Request
	        PanUpdateRequestDTO request = new PanUpdateRequestDTO();

	        request.setApplicationNumber(applicationNo);
	        request.setApiVersion("1.0");
	        request.setApplicationType("Partner");
	        request.setApplicationSubType("Onboarding");
	        request.setInitiatingEntityId(channelId);
	        request.setApp("APIPARTNER");

	        //Action
	        ActionDTO action = new ActionDTO();
	        action.setType("UPDATE");
	        action.setSubType("PAN_NAME_DOB");
	        request.setAction(action);

	        //Financial Details
	        FinancialDetailsDTO financial = new FinancialDetailsDTO();
	        financial.setPanNumber(panNumber);
	        financial.setNameAsPerPan(panName);
	        financial.setDobAsPerPan(formattedDob);

	        //Person
	        PersonPanDTO persons = new PersonPanDTO();
	        persons.setFinancialDetails(financial);

	        request.setPersons(Collections.singletonList(persons));

	        log.info("PAN Update Request :: {}", request);
	        log.info("JSON Request for Pan Update :: {}", mapper.writeValueAsString(request));

	        HttpHeaders headers = util.buildHeaders(
	                httpRequest,
	                tokenManager.getAccessToken(),
	                tokenManager.getAppIdentifierToken(),
	                latitude, longitude
	        );

	        HttpEntity<PanUpdateRequestDTO> entity = new HttpEntity<>(request, headers);
	        
//	        String response = """
//	        		{"applicationNumber":"7245REF177727030109479170","status":"SUCCESS","data":{"status":"Success","message":"Onboarding Data saved successfully.","errorMessage":null}}
//	        		""";
//	        PanUpdateResponseDTO finalResponse = mapper.readValue(response, PanUpdateResponseDTO.class);

	        ResponseEntity<String> response = rest.exchange(saveAgentUrl, HttpMethod.POST, entity, String.class);

	        log.info("PAN Update Raw Response :: {}", response.getBody());
	        PanUpdateResponseDTO finalResponse = mapper.readValue(response.getBody(), PanUpdateResponseDTO.class);
			
	        String errorMsg = (finalResponse.getError() == null || finalResponse.getError().getMessage() == null)
				    ? "No Error Msg" : finalResponse.getError().getMessage();
	        
	        Optional<AgentMasterEntity> optional = agentRepo.findByApplicationNumber(applicationNo);
			if (optional.isPresent()) {
			    AgentMasterEntity agent = optional.get();
			    agent.setStatus(finalResponse.getStatus());
			    agent.setActionType("UPDATE");
			    agent.setActionSubType("PAN_NAME_DOB");
			    Optional.ofNullable(finalResponse).ifPresent(nextAction ->{
			    	if("SUCCESS".equalsIgnoreCase(finalResponse.getStatus()))
			    	{
			    		agent.setNextActionType("AGENT ONBOARDED");
					    agent.setNextActionSubType("COMPLETE");
			    	} else if ("FAILED".equalsIgnoreCase(finalResponse.getStatus())) {
			    		
			    		String errorCode = Optional.ofNullable(finalResponse.getError())
                                .map(ErrorDetails::getCode)
                                .orElse("UNKNOWN");
			    		
			    		switch (errorCode) {

			            case "1001":
			            	agent.setNextActionType("UPDATE");
			            	agent.setNextActionSubType("PAN_NAME_DOB");
			                break;

			            case "1002":
			            	agent.setNextActionType("UPDATE");
			            	agent.setNextActionSubType("PAN_NAME_DOB");
			                break;
			                
			            case "1006":
			                agent.setNextActionType("GENERATE NEW");
			                agent.setNextActionSubType("EXIT");
			                break;    
			                
			            case "1007":
			            	agent.setNextActionType("UPDATE");
			            	agent.setNextActionSubType("PAN_NAME_DOB");
			                break;
			                
			            case "1012":
			            	agent.setNextActionType("UPDATE");
			            	agent.setNextActionSubType("PAN_NAME_DOB");
			            	break;
			            	
			            default:
			            	agent.setNextActionType("UPDATE");
			            	agent.setNextActionSubType("PAN_NAME_DOB");
			                break;
			    		}
			    		
			    	} else {
			    		agent.setNextActionType("UPDATE");
					    agent.setNextActionSubType("PAN_NAME_DOB");
			    	}
			    	
			    });
			    agent.setUpdatedAt(LocalDateTime.now());
			    agentRepo.save(agent);
			    log.info("Details Updated Successfully for Application No :: {}", applicationNo);
			}
			
			InsertSPAgentDetailsEntity spResponse = insertRepo.insertAgentWithLog(

			        finalResponse.getApplicationNumber(),
			        externalRefNo,
			        request.getApiVersion(),
			        request.getInitiatingEntityId(),
			        channelId,
			        externalRefNo,
			        panNumber,
			        panName,
			        null,     // ensure String
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        null,
			        "Mobile",
			        null,
			        null,
			        "AEPS, DMR",
			        true,
			        vkid,
			        "Pan_Update",
			        mapper.writeValueAsString(request),
			        response.getBody(),
//			        response,
			        200,
			        null,
			        finalResponse.getStatus(),
			        errorMsg,
			        null,
			        null,
			        
			        request.getApplicationType(),
			        request.getApplicationSubType(),
			        "Partner",
			        request.getAction().getType(),
			        request.getAction().getSubType()
			);
	        
			log.info("SP Response :: {}", spResponse.toString());
	        return ResponseEntity.ok(finalResponse);

	    } catch (Exception e) {
	        log.error("PAN Update Exception", e);
	        throw new RuntimeException("PAN Update API failed", e);
	    }
	}
	
	//Re-call Application
	@Override
	public ResponseEntity<?> recallApplication(String applicationNo, HttpServletRequest httpRequest, String latitude, String longitude) {

	    log.info("Application No :: {}", applicationNo);
	    ObjectMapper mapper = new ObjectMapper();

	    try {
	    	
	        if (applicationNo == null || applicationNo.isEmpty()) {
	            throw new RuntimeException("Application Number is required");
	        }

	        //Token
	        if (!tokenManager.isAccessTokenValid()) {
	            log.info("Token expired → generating new token");
	            auth.generateToken(httpRequest);
	        }

	        //Request
	        RecallApplicationRequestDTO request = new RecallApplicationRequestDTO();

	        request.setApplicationNumber(applicationNo);
	        request.setApiVersion("1.0");
	        request.setApplicationType("Partner");
	        request.setApplicationSubType("Onboarding");
	        request.setInitiatingEntityId(channelId);
	        request.setApp("APIPARTNER");
	        request.setApplicationStatus("RECALL");

	        //Action
	        ActionDTO action = new ActionDTO();
	        action.setType("UPDATE");
	        action.setSubType("STATUS");
	        request.setAction(action);

	        request.setApplicationStatusReasonDescription("User cancelled onboarding");

	        log.info("JSON Recall Request :: {}", mapper.writeValueAsString(request));

	        //Headers
	        HttpHeaders headers = util.buildHeaders(
	                httpRequest,
	                tokenManager.getAccessToken(),
	                tokenManager.getAppIdentifierToken(),
	                latitude, longitude
	        );

	        HttpEntity<RecallApplicationRequestDTO> entity = new HttpEntity<>(request, headers);

	        ResponseEntity<String> apiResponse = rest.exchange(saveAgentUrl, HttpMethod.POST, entity, String.class);

	        log.info("Recall Raw Response :: {}", apiResponse.getBody());

	        RecallApplicationResponseDTO finalResponse = mapper.readValue(apiResponse.getBody(), RecallApplicationResponseDTO.class);
	        
	        if("Success".equalsIgnoreCase(finalResponse.getStatus())) {
	        	
	        	Optional<AgentMasterEntity> optional = agentRepo.findByApplicationNumber(applicationNo);
    			if (optional.isPresent()) {
    			    AgentMasterEntity agent = optional.get();
//    			    agent.setStatus(finalResponse.getStatus());
    			    agent.setActionType("RECALL");
    			    agent.setActionSubType("Application RE-Call");
    			    Optional.ofNullable(finalResponse.getNextAction()).ifPresent(nextAction ->{
    			    	agent.setNextActionType(nextAction.getType());
    				    agent.setNextActionSubType(nextAction.getSubType());
    			    });
    			    agent.setUpdatedAt(LocalDateTime.now());
    			    agentRepo.save(agent);
    			    log.info("Details Updated Successfully for Application No :: {}", applicationNo);
    			}
	        }
	        
	       return ResponseEntity.ok(finalResponse);

	    } catch (Exception e) {
	        log.error("Recall Application Exception", e);
	        throw new RuntimeException("Recall Application API failed", e);
	    }
	}
	
	//Agent Info Scheduler
	@Scheduled(cron = "0 */10 * * * *") //10 min
	public ResponseEntity<?> agentInfo() {

	    ObjectMapper mapper = new ObjectMapper();
	    
	    MockHttpServletRequest httpRequest = new MockHttpServletRequest();

        // Hardcoded values
        httpRequest.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 Chrome/124.0");
        httpRequest.setRemoteAddr("127.0.0.1");
	    try {
	    	
	    	AgentInfoResponseDTO finalResponse = new AgentInfoResponseDTO();
	    	ErrorDetails error = new ErrorDetails();
	    	List<AgentMasterEntity> allAgents = agentRepo.findLatestPendingJioAgent();
	    	log.info("Agent Details Pending for Onboarding :: {}", allAgents.toString());
	    	
	    	if(allAgents.isEmpty()) {
	    		log.info("No Agents found for Onboarding.......");
	    		error.setCode("200");
	    		error.setMessage("No Agents found for Onboarding");
	    		finalResponse.setError(error);
	    		return ResponseEntity.ok(finalResponse);
	    	}
	    	
	    	for (AgentMasterEntity agent : allAgents) {

                try {
                	
                	//Token
    		        if (!tokenManager.isAccessTokenValid()) {
    		            log.info("Token expired → generating new token");
    		            auth.generateToken(httpRequest);
    		        }

                    String agentId = agent.getExternalAppRefNumber();
                    String latitude = agent.getLatitude().toString();
                    String longitude = agent.getLongitude().toString();
                    
                    log.info("Processing AgentId :: {}", agentId);

                    String url = agentInfoUrl + agentId + "?organizationName=" + channelId;

                    HttpHeaders headers = util.buildHeaders(
                    		httpRequest, tokenManager.getAccessToken(),
                            tokenManager.getAppIdentifierToken(),
                            latitude, longitude
                    );

                    HttpEntity<RecallApplicationRequestDTO> entity = new HttpEntity<>(headers);

                    ResponseEntity<String> apiResponse = rest.exchange(url, HttpMethod.GET, entity, String.class);

                    log.info("Agent Info Raw Response for {} :: {}", agentId, apiResponse.getBody());

                    finalResponse = mapper.readValue(apiResponse.getBody(), AgentInfoResponseDTO.class);

                    // Success Case
                    if (finalResponse.getExternalUserId() != null && agentId.equalsIgnoreCase(finalResponse.getExternalUserId())) {

                        agent.setJioAgentId(finalResponse.getLoginId());
                        agent.setActionType("ONBOARDED");
                        agent.setActionSubType("DONE");
                        agent.setUpdatedAt(LocalDateTime.now());
                        
                        log.info("Agent Updated Successfully :: {}", agentId);

                    } else {

                        // Mismatch Case
                        agent.setActionType("Jio AgentID Pending");
                        agent.setActionSubType("ExternalUserId mismatch or missing");
                        agent.setUpdatedAt(LocalDateTime.now());

                        log.info("ExternalUserId mismatch :: {}", agentId);
                    }

                    // Save after every record
                    agentRepo.save(agent);
                    Thread.sleep(500);

                } catch (Exception ex) {
                    log.error("Failed For Agent :: {}", agent.getExternalAppRefNumber(), ex);
                }
            }
	    	return ResponseEntity.ok(finalResponse);
	        
	    } catch(Exception e) {
	    	log.error("Agent Info Exception", e);
	        throw new RuntimeException("Agent Info API failed", e);
	    }
	}

	//Agent Data + Existing Data to resume Journey
	@Override
	public ResponseEntity<?> agentData(SaveAgentInputDTO request) {
		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> response = new LinkedHashMap<>();
		String journeyType, message = "Agent data fetched successfully";
		
		try {
			
			if (request.getVkid() == null || request.getVkid().trim().isEmpty()) {
	            response.put("status", false);
	            response.put("message", "VKID is required");
	            return ResponseEntity.badRequest().body(response);
	        }

	        String vkid = request.getVkid();

	        String dataQuery = "SELECT * FROM dbo.vw_User_KYC_Details WHERE VK_ID = ?";
	        String existingDataQuery = "EXEC BankingJio.usp_check_agent_present ?";

	        Map<String, Object> agentDetails = jdbc.queryForMap(dataQuery, vkid);
	        log.info("Agent Details :: {}", agentDetails.toString());
	        
	        Object dob = agentDetails.get("DOB");

	        if (dob instanceof java.sql.Date) {
	            agentDetails.put("DOB", ((java.sql.Date) dob).toLocalDate().toString());
	        }
	        
	        Map<String, Object> existingDetails; 
	        
	        try {
	        	existingDetails = jdbc.queryForMap(existingDataQuery, vkid);
	        } catch (EmptyResultDataAccessException ex) {
	        	existingDetails = new LinkedHashMap<>();
	        }
	        
	        boolean hasExistingApplication = existingDetails.get("external_app_ref_number") != null
	                && !existingDetails.get("external_app_ref_number").toString().trim().isEmpty();
	        
	        String nextActionType = Optional.ofNullable(existingDetails.get("next_action_type"))
	                .map(Object::toString)
	                .orElse("");

	        String nextActionSubType = Optional.ofNullable(existingDetails.get("next_action_sub_type"))
	                .map(Object::toString)
	                .orElse("");

	        if ("GENERATE_NEW".equalsIgnoreCase(nextActionType)
	                && "EXIT".equalsIgnoreCase(nextActionSubType)) {

	            journeyType = "NEW_JOURNEY";
	            message = "Starting Fresh Application";

	        } else if ("UPDATE".equalsIgnoreCase(nextActionType)
	                && "PAN_NAME_DOB".equalsIgnoreCase(nextActionSubType)) {

	            journeyType = "PAN_UPDATE";
	            message = "Please proceed with your Pan-Validation";

	        } else if (nextActionType.isBlank() && nextActionSubType.isBlank() && hasExistingApplication) {

	            journeyType = "USE SAME EXT-REF-NO";
	            message = "Resuming Previous Journey";

	        } else if ("AADHAAR".equalsIgnoreCase(nextActionType)
	                && "EKYC-BIOMETRIC".equalsIgnoreCase(nextActionSubType)) {
	        	
	        	journeyType = "EKYC";
	        	message = "Please proceed with EKYC";
	        	
	        } else if ("AGENT ONBOARDED".equalsIgnoreCase(nextActionType)
	                && "COMPLETE".equalsIgnoreCase(nextActionSubType)) {
	        	
	        	journeyType = "Agent Already On-Boarded";
	        	message = "Agent Already On-Boarded";
	        	
	        } else {
	            journeyType = "NEW_JOURNEY";
	            message = "Starting Fresh Application";
	        }

	        response.put("status", true);
	        response.put("message", message);
	        response.put("journeyType", journeyType);
	        response.put("agentDetails", agentDetails);
	        response.put("existingDetails", existingDetails);
	        
	        log.info("JSON Raw Response for Agent Details for VKID :: {} :: {}",vkid, mapper.writeValueAsString(response));
	        
	        return ResponseEntity.ok(response);
						
		} catch (EmptyResultDataAccessException ex) {

	        response.put("status", false);
	        response.put("message", "No data found for VKID : " + request.getVkid());

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {

	        log.error("Agent Data to Mobile Team Exception", e);

	        response.put("status", false);
	        response.put("message", "Agent Data API failed");
	        response.put("error", e.getMessage());

	        return ResponseEntity.ok(response);
	    }
	}
}
