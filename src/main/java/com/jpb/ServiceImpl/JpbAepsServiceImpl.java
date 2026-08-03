package com.jpb.ServiceImpl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.jpb.DTO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.jpb.Config.TokenManager;
import com.jpb.Entity.OnboardingCheckEntity;
import com.jpb.Service.JpbAepsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class JpbAepsServiceImpl implements JpbAepsService {

	@Value("${channelID}")
	private String channelid;

	@Autowired
	UtilityService util;

	@Value("${transactionurl}")
	private String CashDepositeUrl;

	@Value("${GenerateOtp}")
	private String GenerateOtpUrl;

	@Value("${AgentHistory}")
	private String AgentHistoryUrl;

	@Value("${GetAgentInfo}")
	private String GetAgentInfoUrl;

	@Autowired
	AuthServiceImpl auth;

	@Value("${PublicKeyPath}")
	private String keyPath;

	// Helper Services
	@Autowired
	TokenManager tokenManager;

	@Autowired
	UtilityService utilityService;

	@Autowired
	EncryptionService encrypt;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${PublicKeyPath}")
	private String PublicKeyPath;

	private String Token;

	private String authenticationtoken;

	//1=Cash Deposit
//	2=Cash Withdrawal
//			3 = Aadhaar Pay
//			4= Balance Enquiry
//			5= Mini Statement
//			6=Card Transaction
//			7=Reveral
//			8=IMPS
//			9=NEFT
//			10=RTGS
//			11=AEPS
//			12=UPI
//			13=2FA
//			99=Wallet Rechagre From Credit Requset
//			100=Wallet Dedit Requset From Credit Requset
//			102=Wrongly Credit Reversal
	@Override
	public JpbAepsResponseDto CashdepositeGenerateOtp(AepsCommonRequestDto request, HttpServletRequest requesthttp) {

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(requesthttp);
		}


		log.info("request:::"+request.toString());
		AepsTransaction2FaDto aepsrequest = new AepsTransaction2FaDto();

		HttpHeaders header = util.buildHeaders(requesthttp, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		String Discription= "I hereby provide my consent to Jio Payments Bank Limited (\\\"Bank\\\") to use my Aadhaar number and biometric authentication to verify my identity for the purpose of doing AePS transactions from my account (\\\"Service\\\"). JPB has informed me that my biometrics will not be stored/shared and will be submitted to CIDR only for the purpose of authentication. I have reviewed the transaction details and found to be correct. I understand and agree to the terms and conditions governing the Service as available on website www.jiobank.in and confirm that my biometric authentication be treated as my consent for availing the Service from the Bank. I hereby give my consent to receive promotional consent on behalf of the Bank.";


		String consent = Base64.getEncoder().encodeToString(Discription.getBytes(StandardCharsets.UTF_8));

		Users user = new Users();

		aepsrequest.setUser(user);

		aepsrequest.getUser().setEntityType("2");

		aepsrequest.getUser().setUserId("10402611742940734000");
		//aepsrequest.getUser().setUserId(request.getMerchantTranId());

		BankDetails bankDetails = new BankDetails();

		aepsrequest.getUser().setBankDetails(bankDetails);

		//aepsrequest.getUser().getBankDetails().setBankId(request.getNationalBankIdentificationNumber());

		aepsrequest.getUser().getBankDetails().setBankId("876880");

		aepsrequest.setScope("REQUEST");

		AuthenticateDTO authenticatedto = new AuthenticateDTO();

		authenticatedto.setMode(56);

		authenticatedto.setAction("generate");

		AadhaarDTO aadhaar = new AadhaarDTO();

		authenticatedto.setAadhaar(aadhaar);

		authenticatedto.getAadhaar().setNumber("548824449255");

		//authenticatedto.getAadhaar().setNumber(request.getAdhaarNumber());

		authenticatedto.setConsent(consent);

		authenticatedto.setConsentCode("B88");

		List<AuthenticateDTO> Authenticatelist = new ArrayList<>();
		Authenticatelist.add(authenticatedto);

		aepsrequest.setAuthenticateList(Authenticatelist);

		aepsrequest.setPurpose("38");

		//	aepsrequest.setAmount(request.getTransactionAmount());

		BigDecimal amount = BigDecimal.valueOf(5500);
		aepsrequest.setAmount(amount);

		aepsrequest.setExtraInfo("");

		ObjectMapper mapper = new ObjectMapper();

		log.info("headers::" + mapper.writeValueAsString(header));

		log.info("request::" +mapper.writeValueAsString(aepsrequest));

		HttpEntity<AepsTransaction2FaDto> requestentity = new HttpEntity<>(aepsrequest, header);

		ResponseEntity<String> response = restTemplate.exchange(GenerateOtpUrl, HttpMethod.POST, requestentity,
				String.class);

		log.info("response::" + response.getBody());


		JpbAepsResponseDto transactionresponse = new JpbAepsResponseDto();

		JpbAepsResponseDto apiresponse = mapper.readValue(response.getBody(), JpbAepsResponseDto.class);

		transactionresponse.setStatus(apiresponse.getStatus());

		String authenticationtokencode = apiresponse.getOtpReferenceId();

		this.authenticationtoken = authenticationtokencode;

		transactionresponse.setOtpReferenceId(apiresponse.getOtpReferenceId());

		return transactionresponse;
	}

	@Override
	public AepsCommonResponseDto CashDeposite(AepsCommonRequestDto request, HttpServletRequest requesthttp) {

		String ipAddress = requesthttp.getRemoteAddr();

		String timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

//		String xmlBiometricString = """
//<?xml version="1.0"?>
//<PidData>
//  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="34" qScore="89" />
//  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.5.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIIMzNFQTQyNzkwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzE2MDYyMDA5WhcNMjYwNzIzMTIzMDI5WjCBgjEkMCIGCSqGSIb3DQEJARYVc3VwcG9ydEBtYW50cmF0ZWMuY29tMQswCQYDVQQGEwJJTjELMAkGA1UECBMCR0oxEjAQBgNVBAcTCUFobWVkYWJhZDEOMAwGA1UEChMFTVNJUEwxCzAJBgNVBAsTAklUMQ8wDQYDVQQDEwZNRlMxMTAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCYqiwuU8XprB1fTZgmENI72il60qzI9qzZhYFVhxZXpzKrhSuPPo4EPxtvxAwFkXdYLDaArMDgheKjiGNQ1huuPYmMeYA8lcouOT6hiJtCgUsWtFVy75M4BPutRA+1776x7rDhqdC3/UKl5vC8HUAhUeRA5V+FhizSxmfgT1Eowm0IAFeDFXk+eSIXeNakHgIHOO3ZCnAmkvWMWt4svcZ3m7gvvsNFaA02PL6SsWrbewSMyqwAcxe81dLfMWNhM1l9vPgivamwULWSrcoA6EoE8D9yV6UmwVdCi48e+SY1vskFZAn2dKEUG8DGqVlPQ/ZdbpKqCa5HIk9Ey/ZXFWQpAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAC6RyDTBM4vgckN4VAIDawm69jVXJwPECGNgsypMnJXsVXP9oThNagTrpXyYT5vIIOaxFC3Qrh3EUyqtLdtb8GYUdIBKFrryxX5paNdsAZJ97NwDg1H9xkFkclRLPMsE4bmoyTVnb1rl0BQKcv5f5sF/9KQ4iHeKldmL4HWL5SDS5l+ObKqT4Umwq4tlX2GleaElAjxG0cqJ6Cc7BafIDyarVfrVipkg88Lb5ky8OIbO6kFkrZAGVtp7XG2xvLLoW07LwsFm3of7PR38TTSAjApeLdBT5z9GFFJRq1F0lJFcA85av1shNjMBU2ciLsUAxuvTo3ufFm5Tn9z0qNrj7q8=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
//    <additional_info>
//      <Param name="srno" value="7784312" />
//      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
//      <Param name="ts" value="2026-07-22T16:36:03+05:30" />
//      <Param name="modality_type" value="Finger" />
//      <Param name="device_type" value="L1" />
//    </additional_info>
//  </DeviceInfo>
//  <Skey ci="20280825">wk6m9tTL3sgxHzEZbgFt4mo0M4WFhy/M2hdBUWVg3b1JTn5LDPZ+ZTwfcq4gTpWFyuw843PjcbU/v5tnPpQLVQhqOlSMmP/GVp41SGrj3z4as5IGAoc+r4/zMsKBScz0jXUImVirgqthqTFvNYkMa85M1fa9qAu/w1LVRnMAG3sGhyeEn8BPZ2u7tr7010UDsvEN9aftEtCEgsrbbu6D1hmhia/pk4tXkb78aZNEXEjZJVJ1EqhLk1w+R4ndAAQm4ZR67NrQ161vrCtGbjUrbnI0G65V1itl9nQb4F/Xuw0dIHT9M2OysKsOaSo/3o8t9SkwhL2YnzCz7xqA4xLeMg==</Skey>
//  <Hmac>O+QwSHuY89+A+5IKZ4hYCDr1M1Ozo3pkWZVf1RnMT6vXQjits/IkBJyGErUfm2Il</Hmac>
//  <Data type="X">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</Data>
//</PidData>
//								""";

		// String fingerprint = request.getFingerprint();

		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(request.getXmlBiometricString());

		log.info("base64data::" + Base64fingetprintdata);

		AepsTransactionRequestDto aepsrequest = new AepsTransactionRequestDto();

		TransactionAeps transaction = new TransactionAeps();
		aepsrequest.setTransaction(transaction);
		String idempotentKey = String.valueOf(System.currentTimeMillis());

		aepsrequest.getTransaction().setMode(2);
		aepsrequest.getTransaction().setIdempotentKey(idempotentKey);
		log.info(idempotentKey);

		aepsrequest.getTransaction().setCurrency(356);
		aepsrequest.getTransaction().setInvoice(idempotentKey);

		Method method = new Method();
		aepsrequest.getTransaction().setMethod(method);

		aepsrequest.getTransaction().getMethod().setType(217);
		aepsrequest.getTransaction().getMethod().setSubType(550);

		Metadata metadata = new Metadata();

		Agent agent = new Agent();

		aepsrequest.getTransaction().setMetadata(metadata);
		aepsrequest.getTransaction().getMetadata().setAgent(agent);

		//	aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getMerchantTranId());

		aepsrequest.getTransaction().getMetadata().getAgent().setId("10402611742940734000");
		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);

		AddressDTO address = new AddressDTO();

		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode("21");
		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode("440008");

		aepsrequest.getTransaction().setCaptureMethod(1);
		aepsrequest.getTransaction().setLivemode("true");
		aepsrequest.getTransaction().setApplication(channelid);
		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());
		// 2026-07-10T12:02:10.549Z

		InitiatingEntity initiatingEntity = new InitiatingEntity();

		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);

		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(channelid);
		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl("vkmssit.vakrangee.in");

		Amount amount = new Amount();

		aepsrequest.setAmount(amount);

		aepsrequest.getAmount().setNetAmount(request.getTransactionAmount());
		aepsrequest.getAmount().setGrossAmount(request.getTransactionAmount());

		Payee payee = new Payee();

		Mobile mobile = new Mobile();

		aepsrequest.setPayee(payee);

		aepsrequest.getPayee().setMobile(mobile);

		aepsrequest.getPayee().getMobile().setNumber("8237480403");
		aepsrequest.getPayee().getMobile().setCountryCode("91");

		aepsrequest.setPayee(payee);

		aepsrequest.getPayee().setType(13);
		aepsrequest.getPayee().setUserId(null);

		aepsrequest.getPayee().setBankId("100091");
		//aepsrequest.getPayee().setBankId(request.getNationalBankIdentificationNumber());
		aepsrequest.getPayee().setBankName("100091");

		Aadhaar aadhaar = new Aadhaar();

		aepsrequest.getPayee().setAadhaar(aadhaar);

		aepsrequest.getPayee().getAadhaar().setAadhaarNumber(request.getAdhaarNumber());

		ConsentCode consentCode = new ConsentCode();

		aepsrequest.getPayee().getAadhaar().setConsentCode(consentCode);

		aepsrequest.getPayee().getAadhaar().getConsentCode().setId("B88");
		aepsrequest.getPayee().getAadhaar().getConsentCode().setDescription("I hereby provide my consent to Jio Payments Bank Limited (\\\\\\\"Bank\\\\\\\") to use my Aadhaar number and biometric authentication to verify my identity for the purpose of doing AePS transactions from my account (\\\\\\\"Service\\\\\\\"). JPB has informed me that my biometrics will not be stored/shared and will be submitted to CIDR only for the purpose of authentication. I have reviewed the transaction details and found to be correct. I understand and agree to the terms and conditions governing the Service as available on website www.jiobank.in and confirm that my biometric authentication be treated as my consent for availing the Service from the Bank. I hereby give my consent to receive promotional consent on behalf of the Bank.");
		aepsrequest.getPayee().getAadhaar().getConsentCode().setVersion("1");
		aepsrequest.getPayee().getAadhaar().getConsentCode().setTimeStamp(timestamp);

		Secure secure = new Secure();

		aepsrequest.setSecure(secure);
		Biometrics biometric = new Biometrics();

		aepsrequest.getSecure().setBiometrics(biometric);

		// aepsrequest.getSecure().getBiometrics().setFingerprint(request.getFingerprint());
		aepsrequest.getSecure().getBiometrics().setFingerprint(Base64fingetprintdata);
		aepsrequest.getSecure().getBiometrics().setType(1);

		DeviceInfo DeviceInfo = new DeviceInfo();

		aepsrequest.getSecure().setDeviceInfo(DeviceInfo);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("jiobank");

		HeaderDeviceInfoDTO headerDeviceInfoDTO = new HeaderDeviceInfoDTO();

		aepsrequest.getSecure().getDeviceInfo().setSource(headerDeviceInfoDTO);

		aepsrequest.getSecure().getDeviceInfo().getSource().setType("Desktop");

		aepsrequest.getSecure().getDeviceInfo().getSource().setId("0:0:0:0:0:0:0:1");// MY ID NEED TO CONFIRM
		aepsrequest.getSecure().getDeviceInfo().getSource().setIp("0:0:0:0:0:0:0:1");

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsType("WEB");
		aepsrequest.getSecure().getDeviceInfo().getSource().setOsVer("");
		aepsrequest.getSecure().getDeviceInfo().getSource().setModel("");

		GeoLocationDTO location = new GeoLocationDTO();

		aepsrequest.getSecure().getDeviceInfo().setLocation(location);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("biometric device encrypted code");
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLatitude(request.getLatitude());
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLongitude(request.getLongitude());

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(requesthttp);
		}

		HttpHeaders header = util.buildHeaders(requesthttp, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		ObjectMapper mapper = new ObjectMapper();

		log.info("headers::" + mapper.writeValueAsString(header));

		log.info("transaction request: " + mapper.writeValueAsString(aepsrequest));

		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);

//		JioAepsTransactionMasterEntity entity = new JioAepsTransactionMasterEntity();
//
//		entity.setVkId(request.getMerchantUserName());
//		entity.setClientRefId(idempotentKey);
//		entity.setAuthenticationType(1);
//		entity.setCustomerIdentification(request.getAdhaarNumber());
//		entity.setAmount(request.getTransactionAmount());
//		entity.setStatus(0);
//		entity.setDeviceType("1");
//		entity.setMobileNumber(request.getMobileNumber());
//		entity.setIin(request.getNationalBankIdentificationNumber());
//
//		jioAepsTransactionRespository.save(entity);

		ResponseEntity<String> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				String.class);

		log.info("response::" + response.getBody());

		JpbAepsResponseDto apiresponse = mapper.readValue(response.getBody(), JpbAepsResponseDto.class);

		AepsCommonResponseDto responsenew = new AepsCommonResponseDto();


		String responsecode1 = apiresponse.getResponseCode();
		if ("1164".equalsIgnoreCase(responsecode1)) {

			responsenew.setStatusCode(responsecode1);
			responsenew.setMessage(apiresponse.getResponseMessage());
			return responsenew;
		}

		String nextActionRequest = apiresponse.getResponsedata().getTransaction().getNextActionRequest();




		Map<String, Object> map = mapper.readValue(nextActionRequest, Map.class);

		Map<String, Object> transaction1 = (Map<String, Object>) map.get("transaction");
		transaction1.put("idempotentKey", String.valueOf(System.currentTimeMillis()));

		String updatedJson = mapper.writeValueAsString(map);


		log.info("updatedJson::" + updatedJson);

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(requesthttp);
		}

		HttpHeaders headers = util.buildHeaders(requesthttp, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		HttpEntity<String> requestentity2 = new HttpEntity<>(updatedJson, headers);

		ResponseEntity<String> response2 = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity2,
				String.class);

		log.info("response2::" + response2.getBody());

		JpbAepsResponseDto apiresponse2 = mapper.readValue(response2.getBody(), JpbAepsResponseDto.class);

		String responsecode = apiresponse2.getResponseCode();



		if ("1000".equalsIgnoreCase(responsecode)) {

			responsenew.setStatusCode(responsecode);
			responsenew.setMessage(apiresponse2.getResponseMessage());

			return responsenew;

		}

		if ("1117".equalsIgnoreCase(responsecode)) {

			responsenew.setStatusCode(responsecode);
			responsenew.setMessage(apiresponse2.getResponseMessage());

			return responsenew;

		}

		log.info("transaction response: " + mapper.writeValueAsString(apiresponse2));

//			Optional<JioAepsTransactionMasterEntity> optionalentity = jioAepsTransactionRespository
//					.findByClientRefId(idempotentKey);
//
//			if (optionalentity.isPresent()) {
//
//
//				 if (apiresponse2.getResponsedata() != null &&
//					        apiresponse2.getResponsedata().getTransaction() != null) {
//
//
//				entity.setRrnNumber(apiresponse2.getResponsedata().getTransaction().getRrn());
//				entity.setTxnId(apiresponse2.getResponsedata().getTransaction().getTransactionId());
//
//			}
//				entity.setResponseCode(apiresponse2.getResponseCode());
//
//				jioAepsTransactionRespository.save(entity);
//			} else {
//				throw new RuntimeException("Record not found for clientRefId: " + idempotentKey);
//			}

		responsenew.setMessage(apiresponse2.getResponseMessage());

		if(apiresponse2.getResponseCode().equals("00")) {
			responsenew.setStatusCode("10000");
		}

		DataDTO data = new DataDTO();

		responsenew.setData(data);

		data.setRequestTransactionTime(apiresponse2.getResponsedata().getTransaction().getTransactionTime());
		data.setRrn(apiresponse2.getResponsedata().getTransaction().getRrn());
		data.setJioTransactionId(apiresponse2.getResponsedata().getTransaction().getTransactionId());
		data.setResponseCode(apiresponse2.getResponseCode());
		return responsenew;

	}

	@Override
	public AepsCommonResponseDto AepsCashWithdrawal(AepsCommonRequestDto request, HttpServletRequest httpRequest) {

		String ipAddress = httpRequest.getRemoteAddr();

		String timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

//		String xmlBiometricString = """
//<?xml version="1.0"?>
//<PidData>
//  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="30" qScore="80" />
//  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.5.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIINDdBOTJCMkEwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzI0MDQxNjE4WhcNMjYxMDIyMDQzMTA2WjCBgjEkMCIGCSqGSIb3DQEJARYVc3VwcG9ydEBtYW50cmF0ZWMuY29tMQswCQYDVQQGEwJJTjELMAkGA1UECBMCR0oxEjAQBgNVBAcTCUFobWVkYWJhZDEOMAwGA1UEChMFTVNJUEwxCzAJBgNVBAsTAklUMQ8wDQYDVQQDEwZNRlMxMTAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCjpIjDaBfFBr8NSNKoUWhi2ILuBof3XQwo02SeRGbkFmkF4WkIIpw2IV9wbTkdi+PHmxzH6rm4eONVq/Q2Saz//WiJFcJJg4QZPfZwJ46jg+gqUKYSHXg7KGlcQ1l9Uenj0L64GivUsSbuC9IQqV9U5aqCBe0Odt5Wb2x5YnbLy39g//14DnLKqxuhVnVe0tHpTIh/g/jbOXgHSaCGi/B7EYYc4XEUN7fhWtn94P2VWKBiMdRIycSqsCmHnWIc4qqezEXFH+FNCcuLzfVkOgScLCwiMY9z928LhoWy4LYQjBpFZyPGpIUR7PaQ7UsxRgcpVrUuj09pSQRpgp9ScdtxAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAHGgsJrUfEA/edu0Eap5lvGJjBQqifpf4dC6cQ9cDGxF6pb3h9JIZcdpn6l8S6s9KM11z5wrH04SQ6nMwlqWXvS+r46P3y0OgXcnUZxXiIgCREwrIcuGof1cqYXIfQJ50W8yg11a5AAjR8QmFXXxuCfhQRDIl6qbx/ejgos2DMwhAs1Z17XR3k8Vaw+hTlVS1pslT7XAzMvBU/oN7RdFKaYXWEjncThDdn9AR/pePihvNwG3fhAUPPGrkKKA1rOLtMsZFo26XAmx4Gus9C1Ph7MewtEJtkg5lMsEH8mrj8X/H1QlMPYTmsBWIyd3+A3Didu9+L0JLLs/91cibwHvfYU=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
//    <additional_info>
//      <Param name="srno" value="7784312" />
//      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
//      <Param name="ts" value="2026-07-31T11:01:07+05:30" />
//      <Param name="modality_type" value="Finger" />
//      <Param name="device_type" value="L1" />
//    </additional_info>
//  </DeviceInfo>
//  <Skey ci="20280825">cACxC7uAGB7d7yl0k1PxuPu6b4NIDZuY/WOIXKu8lqjTGv+XBMdiiMyqdQHlosphgtFYxO3DFMqMgQrAmX7+QzUOf1aBxm5JhheoIxONsmBXwiGVLbYpWxbMuUJQbJw5ykBqJrVFGSgQFV1KLhRfZFzKk6nodQeJ9AzFCl58rbXwBOEkN6xWg9OAA1x11w2JqDpWVzfYAmQdeyCps7dxVr9jYITFK/UTaBItgB7PwUsyTEXfqemNh+roLI68DjyxsAfA/6B1l/VKJjjMUvO7XENwdE2rJ/Ln1+oG53KvCEvrJxNVwas36akel0ABcG2BBnSg4yKevAf/Am9i7CtunA==</Skey>
//  <Hmac>YOpbC+niTz28/yyPZb78D6GFqUIi95RRT0HEybPLxj4pEXyHMzQhLWzgvGBRGAI8</Hmac>
//  <Data type="X">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</Data>
//</PidData>
//							""";


		//	String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json( xmlBiometricString);

		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(request.getXmlBiometricString());

		AepsTransactionRequestDto aepsrequest = new AepsTransactionRequestDto();

		TransactionAeps transaction = new TransactionAeps();
		aepsrequest.setTransaction(transaction);
		String idempotentKey = String.valueOf(System.currentTimeMillis());

		aepsrequest.getTransaction().setMode(2);
		aepsrequest.getTransaction().setIdempotentKey(idempotentKey);
		log.info(idempotentKey);

		aepsrequest.getTransaction().setCurrency(356);
		aepsrequest.getTransaction().setInvoice("7181344822164814");

		Method method = new Method();
		aepsrequest.getTransaction().setMethod(method);

		aepsrequest.getTransaction().getMethod().setType(115);
		aepsrequest.getTransaction().getMethod().setSubType(550);

		Metadata metadata = new Metadata();

		Agent agent = new Agent();

		aepsrequest.getTransaction().setMetadata(metadata);

		aepsrequest.getTransaction().getMetadata().setAgent(agent);

		aepsrequest.getTransaction().getMetadata().getAgent().setId("10402611742940734000");
		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);

		AddressDTO address = new AddressDTO();

		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode("21");
		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode("440008");

		aepsrequest.getTransaction().setCaptureMethod(1);
		aepsrequest.getTransaction().setLivemode("true");
		aepsrequest.getTransaction().setApplication(channelid);
		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());

		InitiatingEntity initiatingEntity = new InitiatingEntity();

		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);

		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(channelid);
		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl("vkmssit.vakrangee.in");

		Amount amount = new Amount();

		aepsrequest.setAmount(amount);

		aepsrequest.getAmount().setNetAmount(request.getTransactionAmount());
		aepsrequest.getAmount().setGrossAmount(request.getTransactionAmount());

		PayerDto payer = new PayerDto();

		Mobile mobile = new Mobile();

		aepsrequest.setPayer(payer);

		aepsrequest.getPayer().setMobile(mobile);

		aepsrequest.getPayer().getMobile().setMobileNumber("8237480403");
		//aepsrequest.getPayer().getMobile().setMobileNumber(request.getMobileNumber());
		aepsrequest.getPayer().getMobile().setCountryCode("91");

		aepsrequest.setPayer(payer);

		aepsrequest.getPayer().setType(13);
		aepsrequest.getPayer().setUserId(null);
		//aepsrequest.getPayer().setBankId(request.getNationalBankIdentificationNumber());

		if (Integer.parseInt(request.getTransactionAmount()) >= 5000) {
			aepsrequest.getPayer().setBankId("876880");

		}else {

			aepsrequest.getPayer().setBankId("100031");
		}

		aepsrequest.getPayer().setBankName("Jio Payments Bank");

		AadhaarDTO aadhaar = new AadhaarDTO();

		aepsrequest.getPayer().setAadhaar(aadhaar);

		aepsrequest.getPayer().getAadhaar().setAadhaarNumber(request.getAdhaarNumber());

		ConsentDTO consentCode = new ConsentDTO();

		aepsrequest.getPayer().getAadhaar().setConsentCode(consentCode);

		aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");
		aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription("I hereby provide my consent to Jio Payments Bank Limited (\\\"Bank\\\") to use my Aadhaar number and biometric authentication to verify my identity for the purpose of doing AePS transactions from my account (\\\"Service\\\"). JPB has informed me that my biometrics will not be stored/shared and will be submitted to CIDR only for the purpose of authentication. I have reviewed the transaction details and found to be correct. I understand and agree to the terms and conditions governing the Service as available on website www.jiobank.in and confirm that my biometric authentication be treated as my consent for availing the Service from the Bank. I hereby give my consent to receive promotional consent on behalf of the Bank.");
		aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");
		aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());

		Secure secure = new Secure();

		aepsrequest.setSecure(secure);

		if (Integer.parseInt(request.getTransactionAmount()) >= 5000) {

			//	JpbAepsResponseDto response=  CashdepositeGenerateOtp(request,  httpRequest);
			aepsrequest.getSecure().setAuthenticationToken(request.getAuthenticationToken());// add only when amount is
			// above 5000
			log.info("authenticationtoken:::"+authenticationtoken);

			//	aepsrequest.getSecure().setAuthenticationToken(authenticationtoken);
		}
		Biometrics biometric = new Biometrics();

		aepsrequest.getSecure().setBiometrics(biometric);

		// aepsrequest.getSecure().getBiometrics().setFingerprint(request.getFingerprint());
		aepsrequest.getSecure().getBiometrics().setFingerprint(Base64fingetprintdata);
		aepsrequest.getSecure().getBiometrics().setType(1);

		DeviceInfo DeviceInfo = new DeviceInfo();

		aepsrequest.getSecure().setDeviceInfo(DeviceInfo);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("jiobank");

		HeaderDeviceInfoDTO headerDeviceInfoDTO = new HeaderDeviceInfoDTO();

		aepsrequest.getSecure().getDeviceInfo().setSource(headerDeviceInfoDTO);

		aepsrequest.getSecure().getDeviceInfo().getSource().setType("WEB");

		aepsrequest.getSecure().getDeviceInfo().getSource().setId(ipAddress);// MY ID NEED TO CONFIRM
		aepsrequest.getSecure().getDeviceInfo().getSource().setIp(ipAddress);

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsType("web");
		aepsrequest.getSecure().getDeviceInfo().getSource().setOsVer("");
		aepsrequest.getSecure().getDeviceInfo().getSource().setModel("");

		GeoLocationDTO localtion = new GeoLocationDTO();

		aepsrequest.getSecure().getDeviceInfo().setLocation(localtion);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("biometric device encrypted code");
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLatitude(request.getLatitude());
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLongitude(request.getLongitude());

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

	//	Gson gson = new Gson();

		ObjectMapper mapper = new ObjectMapper();

		log.info("headers::" + mapper.writeValueAsString(header));

		log.info("transaction request: " + mapper.writeValueAsString(aepsrequest));

		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);



		ResponseEntity<String> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				String.class);

		log.info("response::" + mapper.writeValueAsString(response.getBody()));



		JpbAepsResponseDto apiresponse = mapper.readValue(response.getBody(), JpbAepsResponseDto.class);

		// JpbAepsResponseDto apiresponse = (JpbAepsResponseDto) response.getBody();

		// log.info("response::" + gson.toJson(apiresponse));

		String responsecode = apiresponse.getResponseCode();

		log.info("transaction response: " + mapper.writeValueAsString(apiresponse));

		AepsCommonResponseDto responsenew =new AepsCommonResponseDto();

		if ("1000".equalsIgnoreCase(responsecode)) {

			responsenew.setStatusCode(responsecode);
			responsenew.setMessage(apiresponse.getResponseMessage());

			return responsenew;

		}

		if ("1117".equalsIgnoreCase(responsecode)) {

			responsenew.setStatusCode(responsecode);
			responsenew.setMessage(apiresponse.getResponseMessage());

			return responsenew;
		}


		responsenew.setMessage(apiresponse.getResponseMessage());
		responsenew.setStatusCode("10000");

		DataDTO data = new DataDTO();

		responsenew.setData(data);

		data.setRequestTransactionTime(apiresponse.getResponsedata().getTransaction().getTransactionTime());
		data.setRrn(apiresponse.getResponsedata().getTransaction().getRrn());
		data.setJioTransactionId(apiresponse.getResponsedata().getTransaction().getTransactionId());
		data.setResponseCode(apiresponse.getResponseCode());

		if (apiresponse.getResponsedata().getAccount() != null
				&& apiresponse.getResponsedata().getAccount().getBalance() != null) {

			data.setBalanceAmount(apiresponse.getResponsedata().getAccount().getBalance());
			data.setTransactionStatus("SUCCESS");
		}

		return responsenew;
	}

	@Override
	public AepsCommonResponseDto AepsMiniStement(AepsCommonRequestDto request, HttpServletRequest httpRequest) {


		String ipAddress = httpRequest.getRemoteAddr();

		String timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

		// String fingerprint = request.getFingerprint();

//		String xmlBiometricString = """
//<?xml version="1.0"?>
//<PidData>
//  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="30" qScore="87" />
//  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.5.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIINDdBOTJCMkEwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzI0MDQxNjE4WhcNMjYxMDIyMDQzMTA2WjCBgjEkMCIGCSqGSIb3DQEJARYVc3VwcG9ydEBtYW50cmF0ZWMuY29tMQswCQYDVQQGEwJJTjELMAkGA1UECBMCR0oxEjAQBgNVBAcTCUFobWVkYWJhZDEOMAwGA1UEChMFTVNJUEwxCzAJBgNVBAsTAklUMQ8wDQYDVQQDEwZNRlMxMTAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCjpIjDaBfFBr8NSNKoUWhi2ILuBof3XQwo02SeRGbkFmkF4WkIIpw2IV9wbTkdi+PHmxzH6rm4eONVq/Q2Saz//WiJFcJJg4QZPfZwJ46jg+gqUKYSHXg7KGlcQ1l9Uenj0L64GivUsSbuC9IQqV9U5aqCBe0Odt5Wb2x5YnbLy39g//14DnLKqxuhVnVe0tHpTIh/g/jbOXgHSaCGi/B7EYYc4XEUN7fhWtn94P2VWKBiMdRIycSqsCmHnWIc4qqezEXFH+FNCcuLzfVkOgScLCwiMY9z928LhoWy4LYQjBpFZyPGpIUR7PaQ7UsxRgcpVrUuj09pSQRpgp9ScdtxAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAHGgsJrUfEA/edu0Eap5lvGJjBQqifpf4dC6cQ9cDGxF6pb3h9JIZcdpn6l8S6s9KM11z5wrH04SQ6nMwlqWXvS+r46P3y0OgXcnUZxXiIgCREwrIcuGof1cqYXIfQJ50W8yg11a5AAjR8QmFXXxuCfhQRDIl6qbx/ejgos2DMwhAs1Z17XR3k8Vaw+hTlVS1pslT7XAzMvBU/oN7RdFKaYXWEjncThDdn9AR/pePihvNwG3fhAUPPGrkKKA1rOLtMsZFo26XAmx4Gus9C1Ph7MewtEJtkg5lMsEH8mrj8X/H1QlMPYTmsBWIyd3+A3Didu9+L0JLLs/91cibwHvfYU=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
//    <additional_info>
//      <Param name="srno" value="7784312" />
//      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
//      <Param name="ts" value="2026-07-29T10:37:04+05:30" />
//      <Param name="modality_type" value="Finger" />
//      <Param name="device_type" value="L1" />
//    </additional_info>
//  </DeviceInfo>
//  <Skey ci="20280825">xptlkZBWhm4izxDtBmEexM9GdobrcZVP9jQ5/l8MU/43Z9+AHuaqt/JCGepNkzBhZ2AoTjSLbs8sL+ZIE6RgPC7jCVxlouXLCHijvSCpgK7tjvsm15r6y8W8tmSQkLP2BQlnEaGa+oOV+XKSJyyzXRIeZfhYp1OEmB44H3S//LTAOvQRqPAdiW5EdOUV6P69om90Buv1EzSXBVy90aDJM3yIr5HbXTBMptFkQ0Akbo0DBqqfHse8SBVJbcJi1n5RPtB+TDcZdF8JKKSUzrOvRpgGyTu/H4lZEdOdU9X+MP3ydWvbaIPHm9Z2OAPTJ4Y3TMLSJ7+osFyXAw0b6l62wg==</Skey>
//  <Hmac>qqsloQ+fnrW5uSoOrTrwjhmjLmdzlVpNuWwQ/vejChyFFDBeUTyszRufC1rPJiKn</Hmac>
//  <Data type="X">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</Data>
//</PidData>
//						  		""";

		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(request.getXmlBiometricString());

		//	String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(xmlBiometricString);

		AepsTransactionRequestDto aepsrequest = new AepsTransactionRequestDto();

		TransactionAeps transaction = new TransactionAeps();

		aepsrequest.setTransaction(transaction);

		String idempotentKey = String.valueOf(System.currentTimeMillis());

		aepsrequest.getTransaction().setIdempotentKey(idempotentKey);

		aepsrequest.getTransaction().setCurrency(356);

		aepsrequest.getTransaction().setInvoice(idempotentKey);

		Method method = new Method();

		aepsrequest.getTransaction().setMethod(method);

		aepsrequest.getTransaction().getMethod().setType(311);

		aepsrequest.getTransaction().getMethod().setSubType(550);

		aepsrequest.getTransaction().setMode(2);

		Metadata metadata = new Metadata();

		aepsrequest.getTransaction().setMetadata(metadata);

		Agent agent = new Agent();

		aepsrequest.getTransaction().getMetadata().setAgent(agent);

		//	aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getMerchantTranId());

		aepsrequest.getTransaction().getMetadata().getAgent().setId("10402611742940734000");

		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);

		AddressDTO address = new AddressDTO();

		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);

//		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());
//
//		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode(request.getStateid());



		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode("440008");

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode("21");

		aepsrequest.getTransaction().setCaptureMethod(1);

		aepsrequest.getTransaction().setLivemode("true");

		aepsrequest.getTransaction().setApplication(channelid);

		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());

		InitiatingEntity initiatingEntity = new InitiatingEntity();

		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);

		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(channelid);

		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl("vkmssit.vakrangee.in");

		PayerDto payer = new PayerDto();

		aepsrequest.setPayer(payer);

		Mobile mobile = new Mobile();

		aepsrequest.getPayer().setMobile(mobile);

		//aepsrequest.getPayer().getMobile().setNumber(request.getMobileNumber());

		aepsrequest.getPayer().getMobile().setNumber("8237480403");

		aepsrequest.getPayer().getMobile().setCountryCode("91");

		aepsrequest.getPayer().setType(13);

		aepsrequest.getPayer().setUserId(null);

		//	aepsrequest.getPayer().setBankId(request.getNationalBankIdentificationNumber());


		aepsrequest.getPayer().setBankId("100012");
		aepsrequest.getPayer().setBankName("Jio Payments Bank");

		AadhaarDTO aadhaar = new AadhaarDTO();

		aepsrequest.getPayer().setAadhaar(aadhaar);

		aepsrequest.getPayer().getAadhaar().setAadhaarNumber(request.getAdhaarNumber());

		ConsentDTO consentCode = new ConsentDTO();

		aepsrequest.getPayer().getAadhaar().setConsentCode(consentCode);

		aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");

		//aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription(request.getDescription());

		aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription("I hereby provide my consent to Jio Payments Bank Limited (\\\"Bank\\\") to use my Aadhaar number and biometric authentication to verify my identity for the purpose of doing AePS transactions from my account (\\\"Service\\\"). JPB has informed me that my biometrics will not be stored/shared and will be submitted to CIDR only for the purpose of authentication. I have reviewed the transaction details and found to be correct. I understand and agree to the terms and conditions governing the Service as available on website www.jiobank.in and confirm that my biometric authentication be treated as my consent for availing the Service from the Bank. I hereby give my consent to receive promotional consent on behalf of the Bank.");

		aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");

		aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());

		Secure secure = new Secure();

		aepsrequest.setSecure(secure);

		Biometrics biometrics = new Biometrics();

		aepsrequest.getSecure().setBiometrics(biometrics);

		aepsrequest.getSecure().getBiometrics().setFingerprint(Base64fingetprintdata);

		aepsrequest.getSecure().getBiometrics().setType(1);

		DeviceInfo deviceInfo = new DeviceInfo();

		aepsrequest.getSecure().setDeviceInfo(deviceInfo);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("biometric device encrypted code");

		HeaderDeviceInfoDTO source = new HeaderDeviceInfoDTO();

		aepsrequest.getSecure().getDeviceInfo().setSource(source);

		aepsrequest.getSecure().getDeviceInfo().getSource().setType("WEB");
		aepsrequest.getSecure().getDeviceInfo().getSource().setId("");

		aepsrequest.getSecure().getDeviceInfo().getSource().setIp(ipAddress);

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsType("Desktop");

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsVer("33");

		aepsrequest.getSecure().getDeviceInfo().getSource().setModel("");

		GeoLocationDTO location = new GeoLocationDTO();

		aepsrequest.getSecure().getDeviceInfo().setLocation(location);

		aepsrequest.getSecure().getDeviceInfo().getLocation().setLatitude(request.getLatitude());
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLongitude(request.getLongitude());

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}

		ObjectMapper mapper = new ObjectMapper();

		log.info("aepsrequest::" + mapper.writeValueAsString(aepsrequest));

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		log.info("header::" + mapper.writeValueAsString(header));

		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);



		ResponseEntity<String> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				String.class);

		log.info("response::" + response.getBody());




		JpbAepsResponseDto apiresponse = mapper.readValue(response.getBody(), JpbAepsResponseDto.class);



		AepsCommonResponseDto transactionresponse = new AepsCommonResponseDto();

		transactionresponse.setStatusCode("10000");
		transactionresponse.setMessage(apiresponse.getResponseMessage());

		DataDTO data = new DataDTO();

		transactionresponse.setData(data);

		data.setRequestTransactionTime(apiresponse.getResponsedata().getTransaction().getTransactionTime());
		data.setRrn(apiresponse.getResponsedata().getTransaction().getRrn());
		data.setJioTransactionId(apiresponse.getResponsedata().getTransaction().getTransactionId());
		data.setResponseCode(apiresponse.getResponseCode());
		if("00".equals(apiresponse.getResponseCode())) {

			if (apiresponse.getResponsedata().getAccount() != null
					&& apiresponse.getResponsedata().getAccount().getBalance() != null) {

				transactionresponse.getData().setBalanceAmount(apiresponse.getResponsedata().getAccount().getBalance());

			}
			List<MiniStatementDto> miniStatementList = new ArrayList<>();

			for(MiniStatementDto statement: apiresponse.getResponsedata().getMiniStatement()) {

				MiniStatementDto dto = new MiniStatementDto();

				dto.setNarration(statement.getTransactionDetails());
				dto.setDate(statement.getTransactionTime());
				dto.setTxnType(statement.getTransactionType());
				dto.setAmount(statement.getAmount());

				miniStatementList.add(dto);
			}

			transactionresponse.getData().setMiniStatementStructureModel(miniStatementList);
		}

		return transactionresponse;
	}

	@Override
	public AepsCommonResponseDto BalanceInquiry(AepsCommonRequestDto request, HttpServletRequest httpRequest) {

		ObjectMapper mapper = new ObjectMapper();

		String ipAddress = httpRequest.getRemoteAddr();

		String timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

//		String xmlBiometricString = """
//<?xml version="1.0"?>
//<PidData>
//  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="32" qScore="90" />
//  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.5.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIINDdBOTJCMkEwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzI0MDQxNjE4WhcNMjYxMDIyMDQzMTA2WjCBgjEkMCIGCSqGSIb3DQEJARYVc3VwcG9ydEBtYW50cmF0ZWMuY29tMQswCQYDVQQGEwJJTjELMAkGA1UECBMCR0oxEjAQBgNVBAcTCUFobWVkYWJhZDEOMAwGA1UEChMFTVNJUEwxCzAJBgNVBAsTAklUMQ8wDQYDVQQDEwZNRlMxMTAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCjpIjDaBfFBr8NSNKoUWhi2ILuBof3XQwo02SeRGbkFmkF4WkIIpw2IV9wbTkdi+PHmxzH6rm4eONVq/Q2Saz//WiJFcJJg4QZPfZwJ46jg+gqUKYSHXg7KGlcQ1l9Uenj0L64GivUsSbuC9IQqV9U5aqCBe0Odt5Wb2x5YnbLy39g//14DnLKqxuhVnVe0tHpTIh/g/jbOXgHSaCGi/B7EYYc4XEUN7fhWtn94P2VWKBiMdRIycSqsCmHnWIc4qqezEXFH+FNCcuLzfVkOgScLCwiMY9z928LhoWy4LYQjBpFZyPGpIUR7PaQ7UsxRgcpVrUuj09pSQRpgp9ScdtxAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAHGgsJrUfEA/edu0Eap5lvGJjBQqifpf4dC6cQ9cDGxF6pb3h9JIZcdpn6l8S6s9KM11z5wrH04SQ6nMwlqWXvS+r46P3y0OgXcnUZxXiIgCREwrIcuGof1cqYXIfQJ50W8yg11a5AAjR8QmFXXxuCfhQRDIl6qbx/ejgos2DMwhAs1Z17XR3k8Vaw+hTlVS1pslT7XAzMvBU/oN7RdFKaYXWEjncThDdn9AR/pePihvNwG3fhAUPPGrkKKA1rOLtMsZFo26XAmx4Gus9C1Ph7MewtEJtkg5lMsEH8mrj8X/H1QlMPYTmsBWIyd3+A3Didu9+L0JLLs/91cibwHvfYU=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
//    <additional_info>
//      <Param name="srno" value="7784312" />
//      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
//      <Param name="ts" value="2026-07-28T12:13:00+05:30" />
//      <Param name="modality_type" value="Finger" />
//      <Param name="device_type" value="L1" />
//    </additional_info>
//  </DeviceInfo>
//  <Skey ci="20280825">qPQynkgtBnB2pZgZREMFhBMk9aCOgveLZxiH+zKeffKWVLeZBcwVYpThofS15x5jrN9THcy2f6Liw0oANRmXjV0SS8Loh+mzzAjrKOnI6tCkziegyRMkAWWThQPhUqUU5tTxUmjJJp5748x0c56LvGbBWY7bIX+honIJjVtEqBTWMQOlWF4FeBcpPvvbVAW1kqfiXNCJVUP8kddw0WKSh/j2jYY5qivxswCF6j4wrtEBoTE+DCFNQqZa2CZxXRp3ZD9goPUBFxEIPlF+qcyTgzScKCPjKYhwb5H9jTTBt+Vgg9s6+7e88kPdvr5H6P/lEyYyPRIv+IYZzJyqhG19NA==</Skey>
//  <Hmac>/RCtUOhbmcbVOD7pxZ7Zwx1PhC9KazvTTxn90zMnciHOlCh7yF9K+kizqedJp6hW</Hmac>
//  <Data type="X">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</Data>
//</PidData>
//						  		""";

		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(request.getXmlBiometricString());

		AepsTransactionRequestDto aepsrequest = new AepsTransactionRequestDto();

		TransactionAeps transaction = new TransactionAeps();

		aepsrequest.setTransaction(transaction);

		String idempotentKey = String.valueOf(System.currentTimeMillis());

		aepsrequest.getTransaction().setIdempotentKey(idempotentKey);

		aepsrequest.getTransaction().setCurrency(356);

		aepsrequest.getTransaction().setInvoice(idempotentKey);

		Method method = new Method();

		aepsrequest.getTransaction().setMethod(method);

		aepsrequest.getTransaction().getMethod().setType(312);

		aepsrequest.getTransaction().getMethod().setSubType(550);

		aepsrequest.getTransaction().setMode(2);

		Metadata metadata = new Metadata();

		aepsrequest.getTransaction().setMetadata(metadata);

		Agent agent = new Agent();

		aepsrequest.getTransaction().getMetadata().setAgent(agent);

		//aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getMerchantTranId());

		aepsrequest.getTransaction().getMetadata().getAgent().setId("10402611742940734000");

		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);

		AddressDTO address = new AddressDTO();

		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);

//		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());
//
//		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode(request.getStateid());

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode("440008");

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode("21");

		aepsrequest.getTransaction().setCaptureMethod(1);

		aepsrequest.getTransaction().setLivemode("true");

		aepsrequest.getTransaction().setApplication(channelid);

		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());

		InitiatingEntity initiatingEntity = new InitiatingEntity();

		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);

		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(channelid);

		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl(null);

		PayerDto payer = new PayerDto();

		aepsrequest.setPayer(payer);

		Mobile mobile = new Mobile();

		aepsrequest.getPayer().setMobile(mobile);

		aepsrequest.getPayer().getMobile().setNumber(request.getMobileNumber());

		aepsrequest.getPayer().getMobile().setCountryCode("91");

		aepsrequest.getPayer().setType(13);

		aepsrequest.getPayer().setUserId(null);

		//aepsrequest.getPayer().setBankId(request.getNationalBankIdentificationNumber());

		aepsrequest.getPayer().setBankId("100011");

		aepsrequest.getPayer().setBankName("Jio Payments Bank");

		AadhaarDTO aadhaar = new AadhaarDTO();

		aepsrequest.getPayer().setAadhaar(aadhaar);

		aepsrequest.getPayer().getAadhaar().setAadhaarNumber(request.getAdhaarNumber());

		ConsentDTO consentCode = new ConsentDTO();

		aepsrequest.getPayer().getAadhaar().setConsentCode(consentCode);

		aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");

		aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription(request.getDescription());

		aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");

		aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());

		Secure secure = new Secure();

		aepsrequest.setSecure(secure);

		Biometrics biometrics = new Biometrics();

		aepsrequest.getSecure().setBiometrics(biometrics);

		aepsrequest.getSecure().getBiometrics().setFingerprint(Base64fingetprintdata);

		aepsrequest.getSecure().getBiometrics().setType(1);

		DeviceInfo deviceInfo = new DeviceInfo();

		aepsrequest.getSecure().setDeviceInfo(deviceInfo);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("biometric device encrypted code");

		HeaderDeviceInfoDTO source = new HeaderDeviceInfoDTO();

		aepsrequest.getSecure().getDeviceInfo().setSource(source);

		aepsrequest.getSecure().getDeviceInfo().getSource().setType("WEB");
		aepsrequest.getSecure().getDeviceInfo().getSource().setId("");

		aepsrequest.getSecure().getDeviceInfo().getSource().setIp(ipAddress);

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsType("Desktop");

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsVer("33");

		aepsrequest.getSecure().getDeviceInfo().getSource().setModel("");

		GeoLocationDTO location = new GeoLocationDTO();

		aepsrequest.getSecure().getDeviceInfo().setLocation(location);

		aepsrequest.getSecure().getDeviceInfo().getLocation().setLatitude(request.getLatitude());
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLongitude(request.getLongitude());

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}


//		JioAepsTransactionMasterEntity entity = new JioAepsTransactionMasterEntity();
//
//		entity.setVkId(request.getMerchantUserName());
//		entity.setClientRefId(idempotentKey);
//		entity.setTransactionType(4);
//		entity.setAuthenticationType(1);
//		entity.setCustomerIdentification(request.getAdhaarNumber());
//	//	entity.setAmount(request.getGrossAmount());
//		entity.setStatus(0);
//		entity.setDeviceType("1");
//		entity.setMobileNumber(request.getMobileNumber());
//		entity.setIin(request.getNationalBankIdentificationNumber());
//
//		jioAepsTransactionRespository.save(entity);

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		log.info("request::" + mapper.writeValueAsString(aepsrequest));

		log.info("headers::" + mapper.writeValueAsString(header));

		log.info("transaction request: " +mapper.writeValueAsString(aepsrequest));

		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);

		ResponseEntity<String> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST,
				requestentity, String.class);


		JpbAepsResponseDto apiresponse = mapper.readValue(response.getBody(), JpbAepsResponseDto.class);

		log.info("response::" + mapper.writeValueAsString(apiresponse));

//		Optional<JioAepsTransactionMasterEntity> optionalentity = jioAepsTransactionRespository
//				.findByClientRefId(idempotentKey);
//
//		if (optionalentity.isPresent()) {
//
//			entity.setRrnNumber(apiresponse.getResponsedata().getTransaction().getRrn());
//			entity.setTxnId(apiresponse.getResponsedata().getTransaction().getTransactionId());
//			entity.setResponseCode(apiresponse.getResponseCode());
//
//			jioAepsTransactionRespository.save(entity);
//		} else {
//			throw new RuntimeException("Record not found for clientRefId: " + idempotentKey);
//		}

		//	JpbAepsResponseDto transactionresponse = new JpbAepsResponseDto();
		AepsCommonResponseDto transactionresponse =new AepsCommonResponseDto();


		transactionresponse.setMessage(apiresponse.getResponseMessage());

		if(apiresponse.getResponseMessage().equals("SUCCESS")) {
			transactionresponse.setStatusCode("10000");
		}else {
			transactionresponse.setStatusCode(apiresponse.getResponseCode());
		}
		DataDTO data = new DataDTO();

		transactionresponse.setData(data);

		data.setRequestTransactionTime(apiresponse.getResponsedata().getTransaction().getTransactionTime());
		data.setRrn(apiresponse.getResponsedata().getTransaction().getRrn());
		data.setJioTransactionId(apiresponse.getResponsedata().getTransaction().getTransactionId());
		data.setResponseCode(apiresponse.getResponseCode());

		if (apiresponse.getResponsedata().getAccount() != null
				&& apiresponse.getResponsedata().getAccount().getBalance() != null) {

			data.setBalanceAmount(apiresponse.getResponsedata().getAccount().getBalance());
			data.setTransactionStatus("SUCCESS");
		}
		return transactionresponse;

	}

	@Override
	public JpbAepsResponseDto AgentHistory(AepsCommonRequestDto request, HttpServletRequest httpRequest) {

		ObjectMapper mapper = new ObjectMapper();

		System.out.println("request::" + request);
		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		HttpEntity<AepsCommonRequestDto> requestentity = new HttpEntity<>(request, header);

		ResponseEntity<JpbAepsResponseDto> response = restTemplate.exchange(AgentHistoryUrl, HttpMethod.POST,
				requestentity, JpbAepsResponseDto.class);

		System.out.println("response::" + mapper.writeValueAsString(response));

		return null;
	}

	@Override
	public JpbAepsResponseDto AgentInfo(AepsCommonRequestDto request,HttpSession session,HttpServletRequest httpRequest) {

		String agentid = request.getMerchantTranId();

		String organizationname = request.getOrgname();

		System.out.println("request::" + request);
		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		HttpEntity<AepsCommonRequestDto> requestentity = new HttpEntity<>(request, header);

//		String url = UriComponentsBuilder.fromUriString(GetAgentInfoUrl + "/" + agentid )
//				.queryParam(channelid).toUriString();

		String url = GetAgentInfoUrl + "/"+agentid;

		log.info("urlagentinfo::" + url);

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestentity, String.class);


		ObjectMapper mapper = new ObjectMapper();

		JpbAepsResponseDto apiresponse = mapper.readValue(response.getBody(), JpbAepsResponseDto.class);

		String Token =  apiresponse.getAadhaar().getToken();

		log.info("newtoken::"+Token);
		session.setAttribute("token", Token);


		System.out.println("response::" + mapper.writeValueAsString(response.getBody()));
		return apiresponse;
	}

	@Override
	public JpbAepsResponseDto AepsFundTransfer(AepsCommonRequestDto request, HttpServletRequest httpRequest) {


		ObjectMapper mapper = new ObjectMapper();

		String ipAddress = httpRequest.getRemoteAddr();

		// Instant timestamp = Instant.now();

		String timestamp = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS)
				.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));

		String fingerprint = """
				<?xml version="1.0"?>
				<PidData>
				  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="41" qScore="87" />
				  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.4.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIIMzNFQTQyNzkwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzA2MDUzNDA4WhcNMjYwNzIzMTIzMDI5WjCBgjEPMA0GA1UEAxMGTUZTMTEwMQswCQYDVQQLEwJJVDEOMAwGA1UEChMFTVNJUEwxEjAQBgNVBAcTCUFobWVkYWJhZDELMAkGA1UECBMCR0oxCzAJBgNVBAYTAklOMSQwIgYJKoZIhvcNAQkBFhVzdXBwb3J0QG1hbnRyYXRlYy5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCYqiwuU8XprB1fTZgmENI72il60qzI9qzZhYFVhxZXpzKrhSuPPo4EPxtvxAwFkXdYLDaArMDgheKjiGNQ1huuPYmMeYA8lcouOT6hiJtCgUsWtFVy75M4BPutRA+1776x7rDhqdC3/UKl5vC8HUAhUeRA5V+FhizSxmfgT1Eowm0IAFeDFXk+eSIXeNakHgIHOO3ZCnAmkvWMWt4svcZ3m7gvvsNFaA02PL6SsWrbewSMyqwAcxe81dLfMWNhM1l9vPgivamwULWSrcoA6EoE8D9yV6UmwVdCi48e+SY1vskFZAn2dKEUG8DGqVlPQ/ZdbpKqCa5HIk9Ey/ZXFWQpAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAAxOWC7h7+DNIp/2vmbZHuIxRQesU6+xlcHW4p0jp4rYLWZKumN7NOf2EVcWz78i1OCaBgmtNNxZ+rreNdcylpvmnspTLk/ovUMDDeZnPdlAOBcvy3f/uUroLNH0gu+bMqoMGkO4qSYDPhrklwXxOnX5Pbx0lBjXWWzBS+5SxRhBmrxYcUDa2EYZ1FHc5rljTDj07Cp6VjnEyeilMzOgqarfGBVobSg78War/VdQ3DRV7OSoopg1fcREC7gbIXTZ7XlVTVjEnQGM2b8C0Tmz7TugzXPx6Thl9Ola36up8jV+p/RT/x1oi8yJzRXdmbXbvczxYnZw7J+6xCN/eYNHkwY=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
				    <additional_info>
				      <Param name="srno" value="7784312" />
				      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
				      <Param name="ts" value="2026-07-10T18:29:05+05:30" />
				      <Param name="modality_type" value="Finger" />
				      <Param name="device_type" value="L1" />
				    </additional_info>
				  </DeviceInfo>
				  <Skey ci="20280825">Gk2PLRt2PdqalxUiBYTo/ghvUCbAUzQFh1rCOtado+BriNMzzdtOGUf9efzfAoyTP7tmZDmQO5I65hRe52tO8L8+OLYWXcVvn5g5bmhUjQUnMJZorGH+CdEaZC8qOLeTZ5e+qw96CqTOBar/9S3XO5+xK3I5CwghjMXmuAiwB0BvSN5fioowArmj1QqZp6Rni6dgN7REqcLNwFB/PmshOto9QKsFmma08bdbz7/CqhH12Z2bIw/xsVdkVphM/B5axRtaCmaybQyI+yDzcC4Cm2O8L8mDeitJhLWwwQNrBsoqEaoqn56NfN4vGsHggfGzP0S5QRleDN54M2GXKxYYRQ==</Skey>
				  <Hmac>se4e/SfI1A5CVxMaun7OiYS6Wqn/qsPoeGZK8fW1cSOcA58IRuosISEj7V/9ppCe</Hmac>
				  <Data type="X">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</Data>
				</PidData>
					  		""";

		// String fingerprint = request.getFingerprint();

		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(fingerprint);

		AepsTransactionRequestDto aepsrequest = new AepsTransactionRequestDto();

		TransactionAeps transaction = new TransactionAeps();

		aepsrequest.setTransaction(transaction);
		String idempotentKey = String.valueOf(System.currentTimeMillis());

		aepsrequest.getTransaction().setIdempotentKey(idempotentKey);
		aepsrequest.getTransaction().setCurrency(356);
		aepsrequest.getTransaction().setInvoice(idempotentKey);

		Method method = new Method();

		aepsrequest.getTransaction().setMethod(method);

		aepsrequest.getTransaction().getMethod().setType(424);
		aepsrequest.getTransaction().getMethod().setSubType(550);
		aepsrequest.getTransaction().setMode(2);

		Metadata metadata = new Metadata();

		Agent agent = new Agent();

		aepsrequest.getTransaction().setMetadata(metadata);

		aepsrequest.getTransaction().getMetadata().setAgent(agent);

		aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getMerchantTranId());

		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);

		AddressDTO address = new AddressDTO();

		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());
		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getStateid());

		aepsrequest.getTransaction().setCaptureMethod(1);

		aepsrequest.getTransaction().setLivemode("true");
		aepsrequest.getTransaction().setApplication(channelid);

		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());

		InitiatingEntity initiatingEntity = new InitiatingEntity();

		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);

		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(channelid);

		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl(null);

		Amount amount = new Amount();

		aepsrequest.setAmount(amount);

		aepsrequest.getAmount().setNetAmount(request.getTransactionAmount());

		aepsrequest.getAmount().setGrossAmount(request.getTransactionAmount());

		PayerDto payer = new PayerDto();

		aepsrequest.setPayer(payer);

		Mobile mobile = new Mobile();

		aepsrequest.getPayer().setMobile(mobile);

		aepsrequest.getPayer().getMobile().setNumber("8237480403");

		aepsrequest.getPayer().getMobile().setCountryCode("91");

		aepsrequest.getPayer().setType(13);

		aepsrequest.getPayer().setUserId(null);

		aepsrequest.getPayer().setBankId("100091");

		aepsrequest.getPayer().setBankName("Jio Payments Bank");

		AadhaarDTO aadhaar = new AadhaarDTO();

		aepsrequest.getPayer().setAadhaar(aadhaar);

		aepsrequest.getPayer().getAadhaar().setAadhaarNumber("548824449255");

		ConsentDTO consentCode = new ConsentDTO();

		aepsrequest.getPayer().getAadhaar().setConsentCode(consentCode);

		aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");
		;

		aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription(request.getDescription());

		aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");

		aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());

		Secure secure = new Secure();

		aepsrequest.setSecure(secure);

		Biometrics biometrics = new Biometrics();

		aepsrequest.getSecure().setBiometrics(biometrics);

		aepsrequest.getSecure().getBiometrics().setFingerprint(Base64fingetprintdata);

		aepsrequest.getSecure().getBiometrics().setType(1);

		DeviceInfo deviceInfo = new DeviceInfo();

		aepsrequest.getSecure().setDeviceInfo(deviceInfo);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("biometric device encrypted code");

		HeaderDeviceInfoDTO source = new HeaderDeviceInfoDTO();

		aepsrequest.getSecure().getDeviceInfo().setSource(source);

		aepsrequest.getSecure().getDeviceInfo().getSource().setType("Web");

		aepsrequest.getSecure().getDeviceInfo().getSource().setIp(ipAddress);

		aepsrequest.getSecure().getDeviceInfo().getSource().setId("");

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsType("Desktop");

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsVer("33");

		aepsrequest.getSecure().getDeviceInfo().getSource().setModel("Xiaomi-2312DRAABI");

		GeoLocationDTO localtion = new GeoLocationDTO();

		aepsrequest.getSecure().getDeviceInfo();

		aepsrequest.getSecure().getDeviceInfo().setLocation(localtion);

		aepsrequest.getSecure().getDeviceInfo().getLocation().setLatitude(request.getLatitude());

		aepsrequest.getSecure().getDeviceInfo().getLocation().setLongitude(request.getLongitude());

		Payee payee = new Payee();

		aepsrequest.setPayee(payee);

		Aadhaar aadhaarpayee = new Aadhaar();

		aepsrequest.getPayee().setAadhaar(aadhaarpayee);

		aepsrequest.getPayee().getAadhaar().setAadhaarNumber(request.getAdhaarNumber());

		ConsentCode consentCodepayee = new ConsentCode();

		aepsrequest.getPayee().getAadhaar().setConsentCode(consentCodepayee);

		aepsrequest.getPayee().getAadhaar().getConsentCode().setId("B88");

		aepsrequest.getPayee().getAadhaar().getConsentCode().setDescription(request.getDescription());

		aepsrequest.getPayee().getAadhaar().getConsentCode().setVersion("1");

		aepsrequest.getPayee().getAadhaar().getConsentCode().setTimeStamp("");

		aepsrequest.getPayee().setBankId(request.getNationalBankIdentificationNumber());

		aepsrequest.getPayee().setBankName("Jio Payments Bank");

		aepsrequest.getPayee().setType(13);


		log.info("request::" + mapper.writeValueAsString(aepsrequest));

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		log.info("aepsrequest fund transfer::" + mapper.writeValueAsString(aepsrequest));

		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);

		ResponseEntity<JpbAepsResponseDto> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST,
				requestentity, JpbAepsResponseDto.class);

		log.info("response::" + mapper.writeValueAsString(response));
		JpbAepsResponseDto apiresponse = response.getBody();

		log.info("response::" + mapper.writeValueAsString(apiresponse));

		return null;
	}

	public JpbAepsResponseDto eAuthenticate(AepsCommonRequestDto request,HttpSession session, HttpServletRequest httpRequest) throws Exception {

		String xmlBiometricString = """
<?xml version="1.0"?>
<PidData>
  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="29" qScore="76" />
  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.5.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIINDdBOTJCMkEwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzI0MDQxNjE4WhcNMjYxMDIyMDQzMTA2WjCBgjEkMCIGCSqGSIb3DQEJARYVc3VwcG9ydEBtYW50cmF0ZWMuY29tMQswCQYDVQQGEwJJTjELMAkGA1UECBMCR0oxEjAQBgNVBAcTCUFobWVkYWJhZDEOMAwGA1UEChMFTVNJUEwxCzAJBgNVBAsTAklUMQ8wDQYDVQQDEwZNRlMxMTAwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCjpIjDaBfFBr8NSNKoUWhi2ILuBof3XQwo02SeRGbkFmkF4WkIIpw2IV9wbTkdi+PHmxzH6rm4eONVq/Q2Saz//WiJFcJJg4QZPfZwJ46jg+gqUKYSHXg7KGlcQ1l9Uenj0L64GivUsSbuC9IQqV9U5aqCBe0Odt5Wb2x5YnbLy39g//14DnLKqxuhVnVe0tHpTIh/g/jbOXgHSaCGi/B7EYYc4XEUN7fhWtn94P2VWKBiMdRIycSqsCmHnWIc4qqezEXFH+FNCcuLzfVkOgScLCwiMY9z928LhoWy4LYQjBpFZyPGpIUR7PaQ7UsxRgcpVrUuj09pSQRpgp9ScdtxAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAHGgsJrUfEA/edu0Eap5lvGJjBQqifpf4dC6cQ9cDGxF6pb3h9JIZcdpn6l8S6s9KM11z5wrH04SQ6nMwlqWXvS+r46P3y0OgXcnUZxXiIgCREwrIcuGof1cqYXIfQJ50W8yg11a5AAjR8QmFXXxuCfhQRDIl6qbx/ejgos2DMwhAs1Z17XR3k8Vaw+hTlVS1pslT7XAzMvBU/oN7RdFKaYXWEjncThDdn9AR/pePihvNwG3fhAUPPGrkKKA1rOLtMsZFo26XAmx4Gus9C1Ph7MewtEJtkg5lMsEH8mrj8X/H1QlMPYTmsBWIyd3+A3Didu9+L0JLLs/91cibwHvfYU=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
    <additional_info>
      <Param name="srno" value="7784312" />
      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
      <Param name="ts" value="2026-07-24T12:09:35+05:30" />
      <Param name="modality_type" value="Finger" />
      <Param name="device_type" value="L1" />
    </additional_info>
  </DeviceInfo>
  <Skey ci="20280825">wayjxbXWNCEjPeAVCwDyJcQDj42azCtxYPDwm5WuPyo2xV+uPmjsL74Xn7tJtMZkgniIWXy9MsJ0iT9vW+yhvDfOI/Sm5mW1E0VYD1g715yv+0gL5615oUxkHlOX0tZiqsgPTikksZJUFwCoiWUWnyu+W9h/AZ25wtL77T8v+MzaUc+N0tz8f4KRFe1jeSPxDDquFdK1GgnkI/Ma4u2yo9vilfZMCQbqflw9AWIEk8GesWbAeLti3XP2x16r8iltO7YTb5F77MTTeW5NIJwOgJ1j2y1D6+F5MMtCliHsau8au0u3/vfHw1LYriF9QMuRC2CdyRQUyktE6VyiQUtIdA==</Skey>
  <Hmac>mbNZgMXQm+C6Lvw9mdfZmtBqFkJlcuWk9gNug6TXrmaX1iwzMeYIGElR1RWiII56</Hmac>
  <Data type="X">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</Data>
</PidData>
		  		""";


		JpbAepsResponseDto response =AgentInfo(request, session, httpRequest);


		String NewToken =  response.getAadhaar().getToken();


		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(xmlBiometricString);
		AepsTransaction2FaDto aepsrequest = new AepsTransaction2FaDto();

		Users user = new Users();

		aepsrequest.setUser(user);

		aepsrequest.getUser().setUserId(request.getMerchantTranId());

		Mobile mobile = new Mobile();

		aepsrequest.getUser().setMobile(mobile);

		aepsrequest.getUser().getMobile().setMobileNumber("8237480403");

		aepsrequest.getUser().getMobile().setCountryCode("91");

		aepsrequest.getUser().setEntityType("2");

		DataDTO name = new DataDTO();

		aepsrequest.getUser().setName(name);

		aepsrequest.getUser().getName().setFirstName("nikhil");

		aepsrequest.getUser().getName().setLastName("raut");

		aepsrequest.setScope("SESSION");

		AuthenticateDTO authenticatedto = new AuthenticateDTO();

		authenticatedto.setMode(10);

		AadhaarDTO aadhaar = new AadhaarDTO();

		authenticatedto.setAadhaar(aadhaar);

		String token = (String) session.getAttribute("token");

		authenticatedto.getAadhaar().setToken(NewToken);

		authenticatedto.setBiometrics(Base64fingetprintdata);

		authenticatedto.setConsent(request.getDescription());

		List<AuthenticateDTO> Authenticatelist = new ArrayList<>();
		Authenticatelist.add(authenticatedto);

		aepsrequest.setAuthenticateList(Authenticatelist);

		aepsrequest.setPurpose("1");

		SecureDTO secure = new SecureDTO();

		aepsrequest.setSecure(secure);


		String aesKey = encrypt.generateRandomString(16);

		String base64PublicKey = new String(Files.readAllBytes(Paths.get(PublicKeyPath)));
		aepsrequest.getSecure().setEncryptionKey(encrypt.encryptRSA(aesKey, base64PublicKey));

		ObjectMapper mapper = new ObjectMapper();

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}




		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());

		log.info("aepsrequest::" + mapper.writeValueAsString(aepsrequest));

		HttpEntity<AepsTransaction2FaDto> requestentity = new HttpEntity<>(aepsrequest, header);

		ResponseEntity<String> newresponse = restTemplate.exchange(GenerateOtpUrl, HttpMethod.POST,
				requestentity, String.class);

		log.info("response::"+newresponse);

		return null;

	}


	public static String generateInvoice() {
		long millis = Instant.now().toEpochMilli(); // 13 digits
		int random = ThreadLocalRandom.current().nextInt(100, 1000); // 3 digits

		return millis + String.valueOf(random); // 16 digits
	}

}
