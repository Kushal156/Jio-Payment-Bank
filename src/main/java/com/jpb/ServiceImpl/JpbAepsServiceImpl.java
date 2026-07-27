package com.jpb.ServiceImpl;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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
import com.jpb.DTO.AadhaarDTO;
import com.jpb.DTO.AddressDTO;
import com.jpb.DTO.AepsCommonRequestDto;
import com.jpb.DTO.AepsTransaction2FaDto;
import com.jpb.DTO.AepsTransactionRequestDto;
import com.jpb.DTO.Agent;
import com.jpb.DTO.Amount;
import com.jpb.DTO.AuthenticateDTO;
import com.jpb.DTO.BankDetails;
import com.jpb.DTO.Biometrics;
import com.jpb.DTO.ConsentDTO;
import com.jpb.DTO.DeviceInfo;
import com.jpb.DTO.GeoLocationDTO;
import com.jpb.DTO.HeaderDeviceInfoDTO;
import com.jpb.DTO.InitiatingEntity;
import com.jpb.DTO.JpbAepsResponseDto;
import com.jpb.DTO.Metadata;
import com.jpb.DTO.Method;
import com.jpb.DTO.Mobile;
import com.jpb.DTO.PayerDto;
import com.jpb.DTO.ResponseData;
import com.jpb.DTO.SecureDTO;
import com.jpb.DTO.TransactionAeps;
import com.jpb.DTO.Users;
import com.jpb.Service.JpbAepsService;

import jakarta.servlet.http.HttpServletRequest;
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
	
	@Override
	public JpbAepsResponseDto CashdepositeGenerateOtp(AepsCommonRequestDto request,HttpServletRequest requesthttp) {
		
		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(requesthttp);
		}
		
		AepsTransaction2FaDto aepsrequest = new AepsTransaction2FaDto();
		
		HttpHeaders header = util.buildHeaders(requesthttp, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
		
		//String consent = utilityService.convertPidXmlToBase64Json(request.getDescription());
		
        String consent = Base64.getEncoder()
                .encodeToString(request.getDescription().getBytes(StandardCharsets.UTF_8));
		
		Users user = new Users();
		
		aepsrequest.setUser(user);
		
		aepsrequest.getUser().setEntityType("2");
		
		aepsrequest.getUser().setUserId(request.getAgentId());
		
		BankDetails bankDetails = new BankDetails();
		
		aepsrequest.getUser().setBankDetails(bankDetails);
		
		aepsrequest.getUser().getBankDetails().setBankId(request.getBankId());
		
		aepsrequest.setScope("REQUEST");
		
		AuthenticateDTO authenticatedto = new  AuthenticateDTO();
		
		authenticatedto.setMode(56);
		
		authenticatedto.setAction("generate");
		
		AadhaarDTO aadhaar = new AadhaarDTO();
		
		authenticatedto.setAadhaar(aadhaar);
		
		authenticatedto.getAadhaar().setNumber(request.getNumber());
		
		authenticatedto.setConsent(consent);
		
		authenticatedto.setConsentCode("B88");
		
		  List<AuthenticateDTO> Authenticatelist = new ArrayList<>();
		  Authenticatelist.add(authenticatedto);
		  
		  aepsrequest.setAuthenticateList(Authenticatelist);
		  
		  aepsrequest.setPurpose("38");
		  
		  aepsrequest.setAmount(request.getAmount());
		  
		  aepsrequest.setExtraInfo("");
		  
			Gson gson = new Gson();
			
			log.info("headers::" + gson.toJson(header));
			
			log.info("request::" + gson.toJson(aepsrequest));
		  
		  HttpEntity<AepsTransaction2FaDto> requestentity = new HttpEntity<>(aepsrequest, header);
		  
			ResponseEntity<String> response = restTemplate.exchange(GenerateOtpUrl, HttpMethod.POST, requestentity,
					String.class);
			
			log.info("response::"+response);
		
		
		return null;
	}

	@Override
	public JpbAepsResponseDto CashDeposite(AepsCommonRequestDto request, HttpServletRequest requesthttp) {

		String ipAddress = requesthttp.getRemoteAddr();
		
		
		  String timestamp = LocalDateTime.now()
	                .truncatedTo(ChronoUnit.MILLIS)
	                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		      
				
		String fingerprints =  """
<?xml version="1.0"?>
<PidData>
  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="49" qScore="87" />
  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.4.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIIMzNFQTQyNzkwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzA2MDUzNDA4WhcNMjYwNzIzMTIzMDI5WjCBgjEPMA0GA1UEAxMGTUZTMTEwMQswCQYDVQQLEwJJVDEOMAwGA1UEChMFTVNJUEwxEjAQBgNVBAcTCUFobWVkYWJhZDELMAkGA1UECBMCR0oxCzAJBgNVBAYTAklOMSQwIgYJKoZIhvcNAQkBFhVzdXBwb3J0QG1hbnRyYXRlYy5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCYqiwuU8XprB1fTZgmENI72il60qzI9qzZhYFVhxZXpzKrhSuPPo4EPxtvxAwFkXdYLDaArMDgheKjiGNQ1huuPYmMeYA8lcouOT6hiJtCgUsWtFVy75M4BPutRA+1776x7rDhqdC3/UKl5vC8HUAhUeRA5V+FhizSxmfgT1Eowm0IAFeDFXk+eSIXeNakHgIHOO3ZCnAmkvWMWt4svcZ3m7gvvsNFaA02PL6SsWrbewSMyqwAcxe81dLfMWNhM1l9vPgivamwULWSrcoA6EoE8D9yV6UmwVdCi48e+SY1vskFZAn2dKEUG8DGqVlPQ/ZdbpKqCa5HIk9Ey/ZXFWQpAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAAxOWC7h7+DNIp/2vmbZHuIxRQesU6+xlcHW4p0jp4rYLWZKumN7NOf2EVcWz78i1OCaBgmtNNxZ+rreNdcylpvmnspTLk/ovUMDDeZnPdlAOBcvy3f/uUroLNH0gu+bMqoMGkO4qSYDPhrklwXxOnX5Pbx0lBjXWWzBS+5SxRhBmrxYcUDa2EYZ1FHc5rljTDj07Cp6VjnEyeilMzOgqarfGBVobSg78War/VdQ3DRV7OSoopg1fcREC7gbIXTZ7XlVTVjEnQGM2b8C0Tmz7TugzXPx6Thl9Ola36up8jV+p/RT/x1oi8yJzRXdmbXbvczxYnZw7J+6xCN/eYNHkwY=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
    <additional_info>
      <Param name="srno" value="7784312" />
      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
      <Param name="ts" value="2026-07-10T17:36:22+05:30" />
      <Param name="modality_type" value="Finger" />
      <Param name="device_type" value="L1" />
    </additional_info>
  </DeviceInfo>
  <Skey ci="20280825">AWDiEN55TeP0OsMy5jflDjdaxviUP0mawuxtkQjCkxz+UhRFU5iDegkN0Gfu5vgRHNNOeBOr+f2gN1BBw15vdLGxVSRu7B0swp1UO5zus05BSE9Dqkyb4TaLhgIHb/6jyf2xKYZ1QINEpUOu1mYgcgs9FzvvngPojd3rimlptFh7WMBgHmX4GCnEAlL/T7XXPjPI/U8ZdaiWPL4j/3gAfsSMzwL3LZfM4VdO8wikdjze1e+E8p47EPxSbN2opOHVg2cu3ohLxrmXWL9Y4lrvmfHjTRMrULZHfFuWVmrI4LDYGqYtfPLhBTWVYTFEaAsCPCKzxyNLSBx1o/ldFEjSpw==</Skey>
  <Hmac>hvxx94o4TEvKMWJOCi48naR9qn7N1/U652J1/JIAhWBvriGY5wqs8GUKcTSwZZnT</Hmac>
  <Data type="X">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</Data>
</PidData>
				""";
				
		//String fingerprint = request.getFingerprint();

		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(fingerprints);

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

		aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getAgentId());
		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);

		AddressDTO address = new AddressDTO();

		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode(request.getStateid());
		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());

		aepsrequest.getTransaction().setCaptureMethod(1);
		aepsrequest.getTransaction().setLivemode("true");
		aepsrequest.getTransaction().setApplication(Integer.parseInt(channelid));
		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());
		//2026-07-10T12:02:10.549Z

		InitiatingEntity initiatingEntity = new InitiatingEntity();

		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);

		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(Integer.parseInt(channelid));
		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl("vkmssit.vakrangee.in");

		Amount amount = new Amount();

		aepsrequest.setAmount(amount);

		aepsrequest.getAmount().setNetAmount(request.getNetAmount());
		aepsrequest.getAmount().setGrossAmount(request.getGrossAmount());

		PayerDto payee = new PayerDto();

		Mobile mobile = new Mobile();

		aepsrequest.setPayee(payee);

		aepsrequest.getPayee().setMobile(mobile);

		aepsrequest.getPayee().getMobile().setNumber(request.getNumber());
		aepsrequest.getPayee().getMobile().setCountryCode("91");

		aepsrequest.setPayee(payee);

		aepsrequest.getPayee().setType(13);
		aepsrequest.getPayee().setUserId(null);
		aepsrequest.getPayee().setBankId(request.getBankId());
		aepsrequest.getPayee().setBankName("100091");

		AadhaarDTO aadhaar = new AadhaarDTO();

		aepsrequest.getPayee().setAadhaar(aadhaar);

		aepsrequest.getPayee().getAadhaar().setAadhaarNumber(request.getAadharNo());

		ConsentDTO consentCode = new ConsentDTO();

		aepsrequest.getPayee().getAadhaar().setConsentCode(consentCode);

		aepsrequest.getPayee().getAadhaar().getConsentCode().setId("B88");
		aepsrequest.getPayee().getAadhaar().getConsentCode().setDescription(request.getDescription());
		aepsrequest.getPayee().getAadhaar().getConsentCode().setVersion("1");
		aepsrequest.getPayee().getAadhaar().getConsentCode().setTimeStamp(Instant.now());

		SecureDTO secure = new SecureDTO();

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

		aepsrequest.getSecure().getDeviceInfo().getSource().setId(ipAddress);// MY ID NEED TO CONFIRM
		aepsrequest.getSecure().getDeviceInfo().getSource().setIp(ipAddress);

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsType(request.getOsType());
		aepsrequest.getSecure().getDeviceInfo().getSource().setOsVer(request.getOsVer());
		aepsrequest.getSecure().getDeviceInfo().getSource().setModel(request.getModel());

		GeoLocationDTO localtion = new GeoLocationDTO();

		aepsrequest.getSecure().getDeviceInfo().setLocation(localtion);

		aepsrequest.getSecure().getDeviceInfo().setPeripheral("biometric device encrypted code");
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLatitude(request.getLatitude());
		aepsrequest.getSecure().getDeviceInfo().getLocation().setLongitude(request.getLongitude());	

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(requesthttp);
		}

		HttpHeaders header = util.buildHeaders(requesthttp, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
		
		Gson gson = new Gson();
	
		log.info("headers::" + gson.toJson(header));

		log.info("transaction request: " + gson.toJson(aepsrequest));
				
		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);		
		
		ResponseEntity<String> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				String.class);
		
		log.info("response::" + response.toString());
		
	
		ObjectMapper mapper = new ObjectMapper();

		JpbAepsResponseDto apiresponse =
		        mapper.readValue(response.getBody(), JpbAepsResponseDto.class);

		String nextActionRequest = apiresponse.getResponsedata()
		        .getTransaction()
		        .getNextActionRequest();

		
		log.info("nextActionRequest::"+nextActionRequest);
		
		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(requesthttp);
		}

		HttpHeaders headers = util.buildHeaders(requesthttp, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
		
		HttpEntity<String> requestentity2 = new  HttpEntity<>(nextActionRequest, headers);
		
		ResponseEntity<String> response2 = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity2,
				String.class);
		
		
	
		log.info("response2::"+ response2 );
		
		
		

		String responsecode = apiresponse.getResponseCode();

		JpbAepsResponseDto transactionresponse = new JpbAepsResponseDto();
		if ("1000".equalsIgnoreCase(responsecode)) {

			transactionresponse.setResponseCode(responsecode);
			transactionresponse.setResponseMessage(apiresponse.getResponseMessage());

			return transactionresponse;

		}
		
		if ("1117".equalsIgnoreCase(responsecode)) {

			transactionresponse.setResponseCode(responsecode);
			transactionresponse.setResponseMessage(apiresponse.getResponseMessage());

			return transactionresponse;

		}
		
		log.info("transaction response: " + gson.toJson(apiresponse));

		transactionresponse.setResponseCode(apiresponse.getResponseCode());
		transactionresponse.setResponseMessage(apiresponse.getResponseMessage());
		
		
		ResponseData responseData = new ResponseData();
		
		transactionresponse.setResponsedata(responseData);
		
		TransactionAeps transactions = new TransactionAeps();
		
		transactionresponse.getResponsedata().setTransaction(transactions);
		
		transactionresponse.getResponsedata().getTransaction().setTransactionTime(apiresponse.getResponsedata().getTransaction().getTransactionTime());;
		
		transactionresponse.getResponsedata().getTransaction().setRrn(apiresponse.getResponsedata().getTransaction().getRrn());
		
		transactionresponse.getResponsedata().getTransaction().setTransactionId( apiresponse.getResponsedata().getTransaction().getTransactionId());
		
		transactionresponse.getResponsedata().getTransaction().setMethodType(apiresponse.getResponsedata().getTransaction().getMethodType());
		
		transactionresponse.getResponsedata().getTransaction().setMethodSubType(apiresponse.getResponsedata().getTransaction().getMethodSubType());
								
		Amount account = new Amount();
		
		transactionresponse.getResponsedata().setAccount(account);
		transactionresponse.getResponsedata().setAccount(apiresponse.getResponsedata().getAccount());

		return transactionresponse;
	}
	
	
	@Override
	public JpbAepsResponseDto AepsCashWithdrawal(AepsCommonRequestDto request, HttpServletRequest httpRequest) {

		String ipAddress = httpRequest.getRemoteAddr();		
		
		//Instant timestamp = Instant.now();
		
		   String timestamp = LocalDateTime.now()
	                .truncatedTo(ChronoUnit.MILLIS)
	                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		        
		
		String fingerprint = request.getFingerprint();

		String Base64fingetprintdata = utilityService.convertPidXmlToBase64Json(fingerprint);
		
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

		aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getAgentId());
		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);

		AddressDTO address = new AddressDTO();

		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);

		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode(request.getStateid());
		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());

		aepsrequest.getTransaction().setCaptureMethod(1);
		aepsrequest.getTransaction().setLivemode("true");
		aepsrequest.getTransaction().setApplication(Integer.parseInt(channelid));
		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());

		InitiatingEntity initiatingEntity = new InitiatingEntity();

		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);

		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(Integer.parseInt(channelid));
		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl("vkmssit.vakrangee.in");

		Amount amount = new Amount();

		aepsrequest.setAmount(amount);

		aepsrequest.getAmount().setNetAmount(request.getNetAmount());
		aepsrequest.getAmount().setGrossAmount(request.getGrossAmount());

		PayerDto payer = new PayerDto();

		Mobile mobile = new Mobile();

		aepsrequest.setPayer(payer);

		aepsrequest.getPayer().setMobile(mobile);

		aepsrequest.getPayer().getMobile().setMobileNumber(request.getNumber());
		aepsrequest.getPayer().getMobile().setCountryCode("91");

		aepsrequest.setPayer(payer);

		aepsrequest.getPayer().setType(13);
		aepsrequest.getPayer().setUserId(null);
		aepsrequest.getPayer().setBankId(request.getBankId());
		aepsrequest.getPayer().setBankName("Jio Payments Bank");

		AadhaarDTO aadhaar = new AadhaarDTO();

		aepsrequest.getPayer().setAadhaar(aadhaar);

		aepsrequest.getPayer().getAadhaar().setAadhaarNumber(request.getAadharNo());

		ConsentDTO consentCode = new ConsentDTO();

		aepsrequest.getPayer().getAadhaar().setConsentCode(consentCode);

		aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");
		aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription(request.getDescription());
		aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");
		aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());

		SecureDTO secure = new SecureDTO();

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

		aepsrequest.getSecure().getDeviceInfo().getSource().setType("WEB");

		aepsrequest.getSecure().getDeviceInfo().getSource().setId(ipAddress);// MY ID NEED TO CONFIRM
		aepsrequest.getSecure().getDeviceInfo().getSource().setIp(ipAddress);

		aepsrequest.getSecure().getDeviceInfo().getSource().setOsType(request.getOsType());
		aepsrequest.getSecure().getDeviceInfo().getSource().setOsVer(request.getOsVer());
		aepsrequest.getSecure().getDeviceInfo().getSource().setModel(request.getModel());

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
		
		Gson gson = new Gson();
		
		log.info("headers::" + gson.toJson(header));
				
		log.info("transaction request: " + gson.toJson(aepsrequest));
		
		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);

		ResponseEntity<String> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				String.class);

		
		log.info("response::" + gson.toJson(response));
		
		ObjectMapper mapper = new ObjectMapper();

		JpbAepsResponseDto apiresponse =
		        mapper.readValue(response.getBody(), JpbAepsResponseDto.class);
		
		//JpbAepsResponseDto apiresponse = (JpbAepsResponseDto) response.getBody();
		
		//log.info("response::" + gson.toJson(apiresponse));

		String responsecode = apiresponse.getResponseCode();

		JpbAepsResponseDto transactionresponse = new JpbAepsResponseDto();
		if ("1000".equalsIgnoreCase(responsecode)) {

			transactionresponse.setResponseCode(responsecode);
			transactionresponse.setResponseMessage(apiresponse.getResponseMessage());

			return transactionresponse;

		}
		
		if ("1117".equalsIgnoreCase(responsecode)) {

			transactionresponse.setResponseCode(responsecode);
			transactionresponse.setResponseMessage(apiresponse.getResponseMessage());

			return transactionresponse;

		}

		log.info("transaction response: " + gson.toJson(apiresponse));

		transactionresponse.setResponseCode(apiresponse.getResponseCode());
		transactionresponse.setResponseMessage(apiresponse.getResponseMessage());
		
		
		ResponseData responseData = new ResponseData();
		
		transactionresponse.setResponsedata(responseData);
		
		TransactionAeps transactions = new TransactionAeps();
		
		transactionresponse.getResponsedata().setTransaction(transactions);
		
		transactionresponse.getResponsedata().getTransaction().setTransactionTime(apiresponse.getResponsedata().getTransaction().getTransactionTime());;
		
		transactionresponse.getResponsedata().getTransaction().setRrn(apiresponse.getResponsedata().getTransaction().getRrn());
		
		transactionresponse.getResponsedata().getTransaction().setTransactionId( apiresponse.getResponsedata().getTransaction().getTransactionId());
		
		transactionresponse.getResponsedata().getTransaction().setMethodType(apiresponse.getResponsedata().getTransaction().getMethodType());
		
		transactionresponse.getResponsedata().getTransaction().setMethodSubType(apiresponse.getResponsedata().getTransaction().getMethodSubType());
								
		Amount account = new Amount();
		
		transactionresponse.getResponsedata().setAccount(account);
		transactionresponse.getResponsedata().getAccount().setBalance(apiresponse.getResponsedata().getAccount().getBalance());
		transactionresponse.getResponsedata().getAccount().setBalance(apiresponse.getResponsedata().getAccount().getAccountExist());
		
		//transactionresponse.

		return transactionresponse;
	}

	@Override
	public JpbAepsResponseDto AepsFundTransfer(AepsCommonRequestDto request,HttpServletRequest httpRequest) {
		
		
		ObjectMapper mapper = new ObjectMapper();
		
     String ipAddress = httpRequest.getRemoteAddr();		
		
		//Instant timestamp = Instant.now();
     
	  String timestamp = LocalDateTime.now()
              .truncatedTo(ChronoUnit.MILLIS)
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
	  
	//	String fingerprint = request.getFingerprint();

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
		
		aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getAgentId());
		
		aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);
	
		AddressDTO address = new AddressDTO();
		
		aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);
		
		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());
		aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getStateid());
		
		aepsrequest.getTransaction().setCaptureMethod(1);
		
		aepsrequest.getTransaction().setLivemode("true");
		aepsrequest.getTransaction().setApplication(Integer.parseInt(channelid));
		
		aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());
		
		InitiatingEntity initiatingEntity = new InitiatingEntity();
		
		aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);
		
		aepsrequest.getTransaction().getInitiatingEntity().setEntityId(Integer.parseInt(channelid));
		
		aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl(null);
		
		Amount amount = new Amount();
		
		aepsrequest.setAmount(amount);
		
		aepsrequest.getAmount().setNetAmount(request.getNetAmount());

		aepsrequest.getAmount().setGrossAmount(request.getGrossAmount());
		
		PayerDto payer = new PayerDto();
		
		aepsrequest.setPayer(payer);
		
		Mobile mobile = new Mobile ();
		
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
		
		aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");;
		
		aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription(request.getDescription());
		
		aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");
		
		aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());
		
		
		SecureDTO secure = new SecureDTO();
		
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
		
		PayerDto payee = new PayerDto();
		
		aepsrequest.setPayee(payee);
		
		AadhaarDTO aadhaarpayee = new AadhaarDTO();
		
		aepsrequest.getPayee().setAadhaar(aadhaarpayee);
		
		aepsrequest.getPayee().getAadhaar().setAadhaarNumber(request.getAadharNo());
		
		ConsentDTO consentCodepayee = new ConsentDTO();
		
		aepsrequest.getPayee().getAadhaar().setConsentCode(consentCodepayee);
		
		aepsrequest.getPayee().getAadhaar().getConsentCode().setId("B88");
		
		aepsrequest.getPayee().getAadhaar().getConsentCode().setDescription(request.getDescription());
		
		aepsrequest.getPayee().getAadhaar().getConsentCode().setVersion("1");
		
		aepsrequest.getPayee().getAadhaar().getConsentCode().setTimeStamp(Instant.now());
		
		aepsrequest.getPayee().setBankId(request.getBankId());
		
		aepsrequest.getPayee().setBankName("Jio Payments Bank");
		
		aepsrequest.getPayee().setType(13);
		
		Gson gson = new Gson();
		
	//	log.info("headers::" + gson.toJson(header));
		
		log.info("request::"+mapper.writeValueAsString(aepsrequest));

		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
		
		log.info("aepsrequest fund transfer::" + gson.toJson(aepsrequest));
		
		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);
		
		ResponseEntity<JpbAepsResponseDto> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				JpbAepsResponseDto.class);

		log.info("response::" + mapper.writeValueAsString(response));
		JpbAepsResponseDto apiresponse = response.getBody();
		
		log.info("response::" + mapper.writeValueAsString(apiresponse));
		
		return null;
	}
	

	@Override
	public JpbAepsResponseDto AepsMiniStement(AepsCommonRequestDto request, HttpServletRequest httpRequest) {
				
	     String ipAddress = httpRequest.getRemoteAddr();		
			
		  String timestamp = LocalDateTime.now()
	                .truncatedTo(ChronoUnit.MILLIS)
	                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		  
		  
	//	  String fingerprint = request.getFingerprint();

		  String fingerprint = """	  		
	<?xml version="1.0"?>
<PidData>
  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="50" qScore="72" />
  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.4.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIIMzNFQTQyNzkwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzA2MDUzNDA4WhcNMjYwNzIzMTIzMDI5WjCBgjEPMA0GA1UEAxMGTUZTMTEwMQswCQYDVQQLEwJJVDEOMAwGA1UEChMFTVNJUEwxEjAQBgNVBAcTCUFobWVkYWJhZDELMAkGA1UECBMCR0oxCzAJBgNVBAYTAklOMSQwIgYJKoZIhvcNAQkBFhVzdXBwb3J0QG1hbnRyYXRlYy5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCYqiwuU8XprB1fTZgmENI72il60qzI9qzZhYFVhxZXpzKrhSuPPo4EPxtvxAwFkXdYLDaArMDgheKjiGNQ1huuPYmMeYA8lcouOT6hiJtCgUsWtFVy75M4BPutRA+1776x7rDhqdC3/UKl5vC8HUAhUeRA5V+FhizSxmfgT1Eowm0IAFeDFXk+eSIXeNakHgIHOO3ZCnAmkvWMWt4svcZ3m7gvvsNFaA02PL6SsWrbewSMyqwAcxe81dLfMWNhM1l9vPgivamwULWSrcoA6EoE8D9yV6UmwVdCi48e+SY1vskFZAn2dKEUG8DGqVlPQ/ZdbpKqCa5HIk9Ey/ZXFWQpAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAAxOWC7h7+DNIp/2vmbZHuIxRQesU6+xlcHW4p0jp4rYLWZKumN7NOf2EVcWz78i1OCaBgmtNNxZ+rreNdcylpvmnspTLk/ovUMDDeZnPdlAOBcvy3f/uUroLNH0gu+bMqoMGkO4qSYDPhrklwXxOnX5Pbx0lBjXWWzBS+5SxRhBmrxYcUDa2EYZ1FHc5rljTDj07Cp6VjnEyeilMzOgqarfGBVobSg78War/VdQ3DRV7OSoopg1fcREC7gbIXTZ7XlVTVjEnQGM2b8C0Tmz7TugzXPx6Thl9Ola36up8jV+p/RT/x1oi8yJzRXdmbXbvczxYnZw7J+6xCN/eYNHkwY=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
    <additional_info>
      <Param name="srno" value="7784312" />
      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
      <Param name="ts" value="2026-07-13T17:38:20+05:30" />
      <Param name="modality_type" value="Finger" />
      <Param name="device_type" value="L1" />
    </additional_info>
  </DeviceInfo>
  <Skey ci="20280825">Lkp9CC445fawGmBNsjHW6KiknWL3kmyYBaKm/qV4wvdgOD26i6EVEm48dAaq9ia37MarYM43PjtZr32p8EPxWx7B0D3VmhU2EcyqFnziuniEXKOUwxkgk8yx6/WLdXaYeno1BhRL4+qLzoqomduSWzDt6ogLov+WbzUv7RrUa3ff8U4yJs8iuXeC+Lfpszc+pRElFo4BAPXhW4Lp0AHYPdJQpE6S97hID+8UGqbgILybWzErvkUY2MT1JArVX5AxyS8wcjiX6Uo0uMinPInn1I657FLwv7rmZiD7A5iwcZKSr3hBaBOJYJ4mqg17vQYbAfWvM00Rq2dwxFuy8n93Dw==</Skey>
  <Hmac>s39Z4UGjhi9NqMJ7YfEB3yVwxTJ8aTWohXP5ll7xrO0lNAdkXzxIjh1GTYLpJlFe</Hmac>
  <Data type="X">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</Data>
</PidData>
		  		""";
		  
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
			
			aepsrequest.getTransaction().getMethod().setType(311);
			
			aepsrequest.getTransaction().getMethod().setSubType(550);
			
			aepsrequest.getTransaction().setMode(2);
			
			Metadata metadata = new Metadata();
			
			aepsrequest.getTransaction().setMetadata(metadata);
			
			Agent agent = new Agent();
			
			aepsrequest.getTransaction().getMetadata().setAgent(agent);
			
			aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getAgentId());
			
			aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);
			
			AddressDTO address = new AddressDTO();
			
			aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);
			
			aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());
			
			aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode(request.getStateid());
				
			aepsrequest.getTransaction().setCaptureMethod(1);
			
			aepsrequest.getTransaction().setLivemode("true");
			
			aepsrequest.getTransaction().setApplication(Integer.parseInt(channelid));
			
			aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());
			
			
			InitiatingEntity initiatingEntity  = new InitiatingEntity();
			
			aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);
			
			aepsrequest.getTransaction().getInitiatingEntity().setEntityId(Integer.parseInt(channelid));
			
			aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl(null);
			
			
          PayerDto payer = new PayerDto();
          
          aepsrequest.setPayer(payer);
          
          Mobile mobile = new  Mobile();
          
          aepsrequest.getPayer().setMobile(mobile);
          
          aepsrequest.getPayer().getMobile().setNumber(request.getNumber());
          
          aepsrequest.getPayer().getMobile().setCountryCode("91");
          
          aepsrequest.getPayer().setType(13);
          
          aepsrequest.getPayer().setUserId(null);
          
          aepsrequest.getPayer().setBankId(request.getBankId());
          
          aepsrequest.getPayer().setBankName("Jio Payments Bank");
          
          AadhaarDTO aadhaar = new AadhaarDTO();
          
          aepsrequest.getPayer().setAadhaar(aadhaar);
          
          aepsrequest.getPayer().getAadhaar().setAadhaarNumber(request.getAadharNo());
          
          
          ConsentDTO consentCode = new ConsentDTO(); 
          
          aepsrequest.getPayer().getAadhaar().setConsentCode(consentCode);
          
          aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");
          
          aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription(request.getDescription());
          
          aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");
          
          aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());
          
          
          SecureDTO secure = new SecureDTO();
          
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

          
          Gson gson = new Gson();	
          
          log.info("aepsrequest11::" + gson.toJson(aepsrequest));
          
      	HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
      	
      	 log.info("header::" + gson.toJson(header));
          
          HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);
          
      	ResponseEntity<String> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				String.class);
      	
       log.info("response::"+response.getBody());
		
		return null;
	}

	
	@Override
	public JpbAepsResponseDto BalanceInquiry(AepsCommonRequestDto request, HttpServletRequest httpRequest) {
		
		Gson gson = new Gson();
		
	     String ipAddress = httpRequest.getRemoteAddr();		
			
		  String timestamp = LocalDateTime.now()
	                .truncatedTo(ChronoUnit.MILLIS)
	                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
		  
		  
		  String fingerprint = """
		  		<?xml version="1.0"?>
<PidData>
  <Resp errCode="0" errInfo="Success." fCount="1" fType="2" nmPoints="44" qScore="100" />
  <DeviceInfo dpId="MANTRA.MSIPL" rdsId="RENESAS.MANTRA.001" rdsVer="1.4.1" mi="MFS110" mc="MIIEADCCAuigAwIBAgIIMzNFQTQyNzkwDQYJKoZIhvcNAQELBQAwgfwxKjAoBgNVBAMTIURTIE1hbnRyYSBTb2Z0ZWNoIEluZGlhIFB2dCBMdGQgMjFVMFMGA1UEMxNMQi0yMDMgU2hhcGF0aCBIZXhhIE9wcG9zaXRlIEd1amFyYXQgSGlnaCBDb3VydCBTLkcgSGlnaHdheSBBaG1lZGFiYWQgLTM4MDA2MDESMBAGA1UECRMJQUhNRURBQkFEMRAwDgYDVQQIEwdHVUpBUkFUMR0wGwYDVQQLExRURUNITklDQUwgREVQQVJUTUVOVDElMCMGA1UEChMcTWFudHJhIFNvZnRlY2ggSW5kaWEgUHZ0IEx0ZDELMAkGA1UEBhMCSU4wHhcNMjYwNzA2MDUzNDA4WhcNMjYwNzIzMTIzMDI5WjCBgjEPMA0GA1UEAxMGTUZTMTEwMQswCQYDVQQLEwJJVDEOMAwGA1UEChMFTVNJUEwxEjAQBgNVBAcTCUFobWVkYWJhZDELMAkGA1UECBMCR0oxCzAJBgNVBAYTAklOMSQwIgYJKoZIhvcNAQkBFhVzdXBwb3J0QG1hbnRyYXRlYy5jb20wggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCYqiwuU8XprB1fTZgmENI72il60qzI9qzZhYFVhxZXpzKrhSuPPo4EPxtvxAwFkXdYLDaArMDgheKjiGNQ1huuPYmMeYA8lcouOT6hiJtCgUsWtFVy75M4BPutRA+1776x7rDhqdC3/UKl5vC8HUAhUeRA5V+FhizSxmfgT1Eowm0IAFeDFXk+eSIXeNakHgIHOO3ZCnAmkvWMWt4svcZ3m7gvvsNFaA02PL6SsWrbewSMyqwAcxe81dLfMWNhM1l9vPgivamwULWSrcoA6EoE8D9yV6UmwVdCi48e+SY1vskFZAn2dKEUG8DGqVlPQ/ZdbpKqCa5HIk9Ey/ZXFWQpAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAAxOWC7h7+DNIp/2vmbZHuIxRQesU6+xlcHW4p0jp4rYLWZKumN7NOf2EVcWz78i1OCaBgmtNNxZ+rreNdcylpvmnspTLk/ovUMDDeZnPdlAOBcvy3f/uUroLNH0gu+bMqoMGkO4qSYDPhrklwXxOnX5Pbx0lBjXWWzBS+5SxRhBmrxYcUDa2EYZ1FHc5rljTDj07Cp6VjnEyeilMzOgqarfGBVobSg78War/VdQ3DRV7OSoopg1fcREC7gbIXTZ7XlVTVjEnQGM2b8C0Tmz7TugzXPx6Thl9Ola36up8jV+p/RT/x1oi8yJzRXdmbXbvczxYnZw7J+6xCN/eYNHkwY=" dc="d24e8a80-3544-4b4b-998f-53176247a457">
    <additional_info>
      <Param name="srno" value="7784312" />
      <Param name="sysid" value="6A3FCBFC2DCDAD2FBFF0" />
      <Param name="ts" value="2026-07-13T17:11:28+05:30" />
      <Param name="modality_type" value="Finger" />
      <Param name="device_type" value="L1" />
    </additional_info>
  </DeviceInfo>
  <Skey ci="20280825">WGS0WsWYDV2AbZ69eg7m7aRZPBtkOrTKmbEGaCTF+gUD42EPUzhxInls9TpJ2W4dYYQmRMnXjhkqN5JPHrYqfnT4/JA3N6tDaHJTuWHXGPEjPo1TLy7zSRFfONFnxVZ452xHkK0ot0eSBBv8AH2WtIX40ynly77SuAOYYnblH9tKbWh1UwpbmHotzYU/JnYHfMHQz+3VbfXCoSyjTinw9F5fZLp8Y0a5eiWTa/EAwpEMMwyaLQnQRJQASyzKSxhEg8u/owV3+2e3Jl4yegVaFVtsskwSYEbfKrTzSVJRJEf9hvCWsdvqC9MYktcGrrG7/vnGWmY9/hA+s5hLbEEt4g==</Skey>
  <Hmac>RSzh3YhH0sxN8XRYSPCheC7FKOwN8sZThy87tsOT99kB6REUc1SNPQ8seajkTxxU</Hmac>
  <Data type="X">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</Data>
</PidData>
		  		""";

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
			
			aepsrequest.getTransaction().getMethod().setType(312);
			
			aepsrequest.getTransaction().getMethod().setSubType(550);
			
			aepsrequest.getTransaction().setMode(2);
			
			Metadata metadata = new Metadata();
			
			aepsrequest.getTransaction().setMetadata(metadata);
			
			Agent agent = new Agent();
			
			aepsrequest.getTransaction().getMetadata().setAgent(agent);
			
			aepsrequest.getTransaction().getMetadata().getAgent().setId(request.getAgentId());
			
			aepsrequest.getTransaction().getMetadata().getAgent().setSubId(null);
			
			AddressDTO address = new AddressDTO();
			
			aepsrequest.getTransaction().getMetadata().getAgent().setAddress(address);
			
			aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setPinCode(request.getPincode());
			
			aepsrequest.getTransaction().getMetadata().getAgent().getAddress().setStateCode(request.getStateid());
				
			aepsrequest.getTransaction().setCaptureMethod(1);
			
			aepsrequest.getTransaction().setLivemode("true");
			
			aepsrequest.getTransaction().setApplication(Integer.parseInt(channelid));
			
			aepsrequest.getTransaction().setInitiatingEntityTimestamp(Instant.now());
			
			
			InitiatingEntity initiatingEntity  = new InitiatingEntity();
			
			aepsrequest.getTransaction().setInitiatingEntity(initiatingEntity);
			
			aepsrequest.getTransaction().getInitiatingEntity().setEntityId(Integer.parseInt(channelid));
			
			aepsrequest.getTransaction().getInitiatingEntity().setCallbackUrl(null);
			
			
         PayerDto payer = new PayerDto();
         
         aepsrequest.setPayer(payer);
         
         
         Mobile mobile = new  Mobile();
         
         aepsrequest.getPayer().setMobile(mobile);
         
         aepsrequest.getPayer().getMobile().setNumber(request.getNumber());
         
         aepsrequest.getPayer().getMobile().setCountryCode("91");
         
         
         
         aepsrequest.getPayer().setType(13);
         
         aepsrequest.getPayer().setUserId(null);
         
         aepsrequest.getPayer().setBankId(request.getBankId());
         
         aepsrequest.getPayer().setBankName("Jio Payments Bank");
         
         AadhaarDTO aadhaar = new AadhaarDTO();
         
         aepsrequest.getPayer().setAadhaar(aadhaar);
         
         aepsrequest.getPayer().getAadhaar().setAadhaarNumber(request.getAadharNo());
         
         
         ConsentDTO consentCode = new ConsentDTO(); 
         
         aepsrequest.getPayer().getAadhaar().setConsentCode(consentCode);
         
         aepsrequest.getPayer().getAadhaar().getConsentCode().setId("B88");
         
         aepsrequest.getPayer().getAadhaar().getConsentCode().setDescription(request.getDescription());
         
         aepsrequest.getPayer().getAadhaar().getConsentCode().setVersion("1");
         
         aepsrequest.getPayer().getAadhaar().getConsentCode().setTimeStamp(Instant.now());
         
         
         SecureDTO secure = new SecureDTO();
         
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

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
		
	
         
         log.info("request::" + gson.toJson(aepsrequest));
         
     
     	log.info("headers::" + gson.toJson(header));


		log.info("transaction request: " + gson.toJson(aepsrequest));
				
		HttpEntity<AepsTransactionRequestDto> requestentity = new HttpEntity<>(aepsrequest, header);

		ResponseEntity<JpbAepsResponseDto> response = restTemplate.exchange(CashDepositeUrl, HttpMethod.POST, requestentity,
				JpbAepsResponseDto.class);
         
		JpbAepsResponseDto apiresponse = response.getBody();
		
		log.info("response::" + gson.toJson(apiresponse));

		String responsecode = apiresponse.getResponseCode();

		JpbAepsResponseDto transactionresponse = new JpbAepsResponseDto();
		if ("1000".equalsIgnoreCase(responsecode)) {

			transactionresponse.setResponseCode(responsecode);
			transactionresponse.setResponseMessage(apiresponse.getResponseMessage());

			return transactionresponse;

		}

		log.info("transaction response: " + gson.toJson(apiresponse));

		transactionresponse.setResponseCode(apiresponse.getResponseCode());
		transactionresponse.setResponseMessage(apiresponse.getResponseMessage());
		transactionresponse.getResponsedata().getTransaction()
				.setTransactionTime(apiresponse.getResponsedata().getTransaction().getTransactionTime());
		transactionresponse.getResponsedata().getTransaction()
				.setRrn(apiresponse.getResponsedata().getTransaction().getRrn());
		transactionresponse.getResponsedata().getAccount()
				.setBalance(apiresponse.getResponsedata().getAccount().getBalance());
		transactionresponse.getResponsedata().getAccount()
				.setBalance(apiresponse.getResponsedata().getAccount().getBalance());

		

		return transactionresponse;
	}

	
	
	
	@Override
	public JpbAepsResponseDto AgentHistory(AepsCommonRequestDto request, HttpServletRequest httpRequest) {
	
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println("request::"+ request);
		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}

		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
				
		
		HttpEntity<AepsCommonRequestDto> requestentity = new HttpEntity<>(request, header);
		
		ResponseEntity<JpbAepsResponseDto> response = restTemplate.exchange(AgentHistoryUrl, HttpMethod.POST, requestentity,
				JpbAepsResponseDto.class);
		
		
		System.out.println("response::"+mapper.writeValueAsString(response));
		
		
		return null;
	}


	@Override
	public JpbAepsResponseDto AgentInfo(AepsCommonRequestDto request, HttpServletRequest httpRequest) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		String agentid =request.getAgentId();
		
		String organizationname = request.getOrgname();
		

		System.out.println("request::"+ request);
		if (!tokenManager.isAccessTokenValid()) {
			log.info("Token expired → generating new token");
			auth.generateToken(httpRequest);
		}
		
		
		HttpHeaders header = util.buildHeaders(httpRequest, tokenManager.getAccessToken(),
				tokenManager.getAppIdentifierToken(), request.getLatitude(), request.getLongitude());
		
		HttpEntity<AepsCommonRequestDto> requestentity = new HttpEntity<>( request,header);
		
		String url = UriComponentsBuilder
		        .fromUriString(GetAgentInfoUrl + "/" + agentid)
		        .queryParam("organizationName", organizationname)
		        .toUriString();
		
		log.info("urlagentinfo::"+ url);
		
		
		ResponseEntity<JpbAepsResponseDto> response = restTemplate.exchange(url,
				HttpMethod.GET, 
				requestentity,
				JpbAepsResponseDto.class
		        );
		
		
		System.out.println("response::"+mapper.writeValueAsString(response));
		return null;
	}
	
    public static String generateInvoice() {
        long millis = Instant.now().toEpochMilli(); // 13 digits
        int random = ThreadLocalRandom.current().nextInt(100, 1000); // 3 digits

        return millis + String.valueOf(random); // 16 digits
    }
	
}
