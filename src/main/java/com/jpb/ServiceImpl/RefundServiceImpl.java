package com.jpb.ServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.jpb.Config.TokenManager;
import com.jpb.DTO.RefundResponseDTO;
import com.jpb.DTO.SubmitApplicationResponseDTO;
import com.jpb.DTO.ActionDTO;
import com.jpb.DTO.BCDetailsDTO;
import com.jpb.DTO.ContactDetailsDTO;
import com.jpb.DTO.CustomerInputRequestDTO;
import com.jpb.DTO.CustomerPanAadharVerifyRequestDTO;
import com.jpb.DTO.ErrorDetails;
import com.jpb.DTO.OrganizationDTO;
import com.jpb.DTO.PersonDTO;
import com.jpb.DTO.RefundCommonRequest;
import com.jpb.DTO.VerifyOtpResponseDTO;
import com.jpb.DTO.VoucherDetailsDTO;
import com.jpb.Entity.AgentMasterEntity;
import com.jpb.Entity.CustomerMasterEntity;
import com.jpb.Entity.CustomerRefundMasterEntity;
import com.jpb.Repository.AgentMasterRepository;
import com.jpb.Repository.CustomerMasterRepository;
import com.jpb.Repository.CustomerRefundMasterRepository;
import com.jpb.Service.RefundService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class RefundServiceImpl implements RefundService {
	
	@Value("${apiVersion}")
	private String apiVersion;
	
	@Value("${applicationType}")
	private String applicationType;
	
	@Value("${channelID}")
	private String channelId;
	
	@Value("${generateOtpURL}")
	private String URL;
	
	@Autowired
	RestTemplate rest;
	
	@Autowired
	UtilityService util;
	
	@Autowired
	TokenManager tokenManager;
	
	@Autowired
	AuthServiceImpl auth;
	
	@Autowired
	CustomerMasterRepository masterRepo;
	
	@Autowired
	AgentMasterRepository agentRepo;
	
	@Autowired
	CustomerRefundMasterRepository refundRepo;

	//Voucher Verify
	@Override
	public ResponseEntity<?> voucherVerify(CustomerInputRequestDTO input, HttpServletRequest httpRequest) {
		log.info("Voucher Verify Request from Customer :: {}", input.toString());
		
		ObjectMapper mapper = new ObjectMapper();
		String agentId = null;
		Integer masterId = null;
		CustomerMasterEntity master = null;
		CustomerRefundMasterEntity refundMaster = null;
		RefundResponseDTO finalResponse = new RefundResponseDTO();
		ErrorDetails error = new ErrorDetails();
		try {
			
			if ((input.getMobileNumber() == null || input.getMobileNumber().length() != 10) && 
					(input.getVoucherCode() == null)) {
				error.setCode("400");
				error.setMessage("Mobile No & Voucher Code Mandatory");
				finalResponse.setStatus("FAILED");
				finalResponse.setError(error);
				return ResponseEntity.ok(finalResponse);
			}
			
			// Token
			if (!tokenManager.isAccessTokenValid()) {
					log.info("Token expired → generating new token");
					auth.generateToken(httpRequest);
			}
			
			agentId = agentRepo.findByVkidAndJioAgentIdIsNotNull(input.getVkid())
	                .map(agent -> {
	                    log.info("Agent Master Details for VKID {} :: {}", input.getVkid(), agent);
	                    return agent.getJioAgentId();
	                })
	                .orElse(null);
			
			String externalRefNo = "JPBR" + System.currentTimeMillis();
			
			//Request
			RefundCommonRequest request = new RefundCommonRequest();
			request.setExternalAppRefNumber(externalRefNo);
			request.setApiVersion(apiVersion);
			request.setApplicationType(applicationType);
			request.setApplicationSubType("Refund");
			request.setInitiatingEntityId(channelId);
			
			//Action
			ActionDTO action = new ActionDTO();
			action.setType("VOUCHER");
			action.setSubType("VERIFY");
			request.setAction(action);
			
			//Person
			PersonDTO person = new PersonDTO();
			ContactDetailsDTO contanct = new ContactDetailsDTO();
			person.setPersonType("INDIVIDUAL");
			contanct.setMobileNumber(input.getMobileNumber());
			person.setContactDetails(List.of(contanct));
			request.setPersons(List.of(person));	
			
			//Organization
			OrganizationDTO org = new OrganizationDTO();
			org.setId(channelId);
			request.setOrganization(org);
			
			//BC Details
			BCDetailsDTO bc = new BCDetailsDTO();
			bc.setUserId(agentId);
			request.setBcDetails(bc);
			
			// Voucher
			VoucherDetailsDTO voucher = new VoucherDetailsDTO();
			voucher.setVoucherCode(input.getVoucherCode());
			request.setVoucherDetails(List.of(voucher));
			
			log.info("JSON Request for Refund Voucher Verify :: {}", mapper.writeValueAsString(request));
			
			HttpHeaders headers = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
					tokenManager.getAppIdentifierToken(), input.getLatitude(), input.getLongitude());

			HttpEntity<RefundCommonRequest> entity = new HttpEntity<>(request, headers);

			ResponseEntity<String> response = null;
			String responseBody = null;
			Integer statusCode = null;
			
			try {
				response = rest.exchange(URL, HttpMethod.POST, entity, String.class);
				log.info("Refund Voucher Verify Raw Response :: {}", response.getBody());
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
					finalResponse = mapper.readValue(responseBody, RefundResponseDTO.class);
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
			
			String errorMsg = (finalResponse.getError() == null || finalResponse.getError().getMessage() == null)
					? "No Error Msg" : finalResponse.getError().getMessage();
			
			//DB Activity-------------------------------------
			refundMaster.setExternalAppRefNumber(externalRefNo);
			refundMaster.setMobileNo(input.getMobileNumber());
			refundMaster.setVoucherCode(input.getVoucherCode());
			refundMaster.setApplicationType(applicationType);
			refundMaster.setApplicationSubType("Refund");
			refundMaster.setActionType("VOUCHER");
			refundMaster.setActionSubType("VERIFY");
			refundMaster.setLatitude(input.getLatitude());
			refundMaster.setLongitude(input.getLongitude());
			refundMaster.setVkid(input.getVkid());
			refundMaster.setJioAgentId(agentId);
			refundMaster.setStage(1);
			refundMaster.setCreateDateTime(LocalDateTime.now());
			
			//Success Response
			if ("SUCCESS".equalsIgnoreCase(finalResponse.getStatus())) {
				
				Optional<CustomerMasterEntity> masterEntity = masterRepo.findByExternalAppRefNumber(input.getExternalAppRefNumber());

				if (masterEntity.isEmpty()) {
					throw new RuntimeException("Customer not found with same Application-No & ExternalRefNo");
				}
				
				//Customer Master
				if (masterEntity.isPresent()) {
					master = masterEntity.get();
					masterId = master.getId();
					log.info("VKID :: {}, row :: {}", input.getVkid(), masterId);

					master.setActionType("VOUCHER");
					master.setActionSubType("VERIFY");
					master.setNextActionType(finalResponse.getNextAction().getType());
					master.setNextActionSubType(finalResponse.getNextAction().getSubType());
					master.setStatus(finalResponse.getStatus());
				}
				
				//History
				
				//API Log
			}
			
			masterRepo.save(master);
			refundRepo.save(refundMaster);
			
			//--------------------------------------------

			return ResponseEntity.ok(response.getBody());
			
		} catch(Exception e) {
			
			log.error("Refund Voucher Verify Exception", e);

			error.setCode("500");
			error.setMessage(e.getMessage());
			finalResponse.setStatus("FAILED");
			finalResponse.setError(error);

			return ResponseEntity.ok(finalResponse);
		}
	}

	//Voucher Redeem
	@Override
	public ResponseEntity<?> voucherRedeem(CustomerInputRequestDTO request, HttpServletRequest httpRequest) {
		
		return null;
	}

}
