package com.jpb.ServiceImpl;

import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.jpb.DTO.AddressDTO;
import com.jpb.DTO.CustomerPanAadharVerifyResponseDTO;
import com.jpb.DTO.DMTDeviceSource;
import com.jpb.DTO.GeoLocationDTO;
import com.jpb.DTO.HeaderDeviceInfoDTO;
import com.jpb.Entity.CustomerMasterEntity;

import jakarta.persistence.Entity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.json.XML;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public final class UtilityService {

	@Value("${AppName}")
	private String appName;
	
	@Value("${channelID}")
	private String channelId;
	
	@Value("${SA}")
	private String sa;
	
	@Value("${appCode}")
	private String appCode;
	
	@Value("${apiKey}")
	private String apiKey;
	
	@Value("${SMSURL}")
	private String smsURL;
	
	@Autowired
	RestTemplate rest;
	
	public void updateHardExit(CustomerMasterEntity master, String errorCode) {

	    switch (errorCode.toUpperCase()) {

	    	case "FTCHAGNTINFO04":
	        case "SNDMBLOTP001":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("DUPLICATE_REQUEST");
	            break;

	        case "FTCHAGNTINFO02":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("AGENT_INACTIVE");
	            break;

	        case "FTCHAGNTINFO03":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("INVALID_AGENT");
	            break;

	        case "FTCHAGNTINFO01":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("AGENT_INFO_ERROR");
	            break;

	        case "1015":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("OUTSIDE_RADIUS");
	            break;

	        case "ELICHK001":
	        case "ELICHK002":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("API_DOWN");
	            break;

	        case "ELICHK003":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("ACC_EXISTS");
	            break;

	        case "ELICHK004":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("ELIGIBILITY_FAILED");
	            break;

	        case "OTP001":
	            master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("OTP_FAILURE");
	            break;
	         
	        case "INFDEDUPE002":
	        	master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("MATCH_FOUND");
	            break;
	            
	        case "ADDR001":
	        case "ADDR002":
	        case "ADDR003":
	        	master.setNextActionType("HARD_EXIT");
	            master.setNextActionSubType("ADDR_ISSUE");
	        	break;
	    }
	}
	
	//Device Info for DMT 
	public String getDMTDeviceInfoJson(HttpServletRequest httpRequest) {

		try {
			String userAgent = httpRequest.getHeader("User-Agent");

			String deviceType = "WEB";
			String os = "WINDOWS";

			if (userAgent != null) {
				String ua = userAgent.toLowerCase();

				if (ua.contains("android") || ua.contains("iphone") || ua.contains("mobile")) {
					deviceType = "MOB";
				}

				if (ua.contains("android"))
					os = "ANDROID";
				else if (ua.contains("iphone"))
					os = "IOS";
				else if (ua.contains("windows"))
					os = "WINDOWS";
				else if (ua.contains("mac"))
					os = "MAC";
			}

			String ipAddress = httpRequest.getHeader("X-Forwarded-For");
			if (ipAddress == null || ipAddress.isEmpty()) {
				ipAddress = httpRequest.getRemoteAddr();
			}

			DMTDeviceSource deviceInfo = new DMTDeviceSource();
			deviceInfo.setIp(ipAddress);
			deviceInfo.setType(deviceType);
			deviceInfo.setOsType(os);
			deviceInfo.setOsVer("1.0");
			deviceInfo.setModel(os);
			deviceInfo.setId("1");
			
			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(deviceInfo);

		} catch (Exception e) {
			throw new RuntimeException("Error building device info", e);
		}
	}
	
	// Header for Device Info
	public String getDeviceInfoJson(HttpServletRequest httpRequest, String latitude, String longitude) {

		try {
			String userAgent = httpRequest.getHeader("User-Agent");

			String deviceType = "WEB";
			String os = "WINDOWS";

			if (userAgent != null) {
				String ua = userAgent.toLowerCase();

				if (ua.contains("android") || ua.contains("iphone") || ua.contains("mobile")) {
					deviceType = "MOB";
				}

				if (ua.contains("android"))
					os = "ANDROID";
				else if (ua.contains("iphone"))
					os = "IOS";
				else if (ua.contains("windows"))
					os = "WINDOWS";
				else if (ua.contains("mac"))
					os = "MAC";
			}

			String ipAddress = httpRequest.getHeader("X-Forwarded-For");
			if (ipAddress == null || ipAddress.isEmpty()) {
				ipAddress = httpRequest.getRemoteAddr();
			}

			HeaderDeviceInfoDTO deviceInfo = new HeaderDeviceInfoDTO();
			deviceInfo.setIpAddress(ipAddress);
			deviceInfo.setType(deviceType);
			deviceInfo.setOs(os);
			deviceInfo.setAppName(appName);
			deviceInfo.setAppId("APP001");
			deviceInfo.setSdkVersion("1.0");
			deviceInfo.setMobile("9999999999");
			deviceInfo.setUserAgent(userAgent);
			
			//new
			GeoLocationDTO geoDto = new GeoLocationDTO();
			geoDto.setName(null);
//			geoDto.setLatitude("28.4210");
//			geoDto.setLongitude("77.1100");
			geoDto.setLatitude(latitude);
			geoDto.setLongitude(longitude);
			deviceInfo.setLocation(geoDto);

			ObjectMapper mapper = new ObjectMapper();
			return mapper.writeValueAsString(deviceInfo);

		} catch (Exception e) {
			throw new RuntimeException("Error building device info", e);
		}
	}

	// Header for the token
	public HttpHeaders buildHeaders(HttpServletRequest request, String accessToken,
			String appIdToken, String latitude, String longitude) {

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		//Caps for AGENT && small-case for CUSTOMER
//		headers.set("X-DEVICE-INFO", getDeviceInfoJson(request));
		headers.set("x-device-info", getDeviceInfoJson(request, latitude, longitude));
		headers.set("x-channel-id", channelId);
		headers.set("x-trace-id", UUID.randomUUID().toString());

		if (accessToken != null) {
			headers.set("x-app-access-token", accessToken);
		}

		if (appIdToken != null) {
			headers.set("x-appid-token", appIdToken);
		}

		return headers;
	}
	
	// Bio metric string to json and base64 conversion for DMT updated Timestamp
		public String DMTconvertPidXmlToBase64Json(String xmlData) {
			    try {
			        //XML → JSON
			        JSONObject xmlJson = XML.toJSONObject(xmlData);
			        JSONObject pidData = xmlJson.optJSONObject("PidData");
			        
			        String consent = "I hereby provide my consent to Jio Payments Bank Limited (Bank) to "
			        		+ "use my Aadhaar number and biometric authentication to verify my identity for"
			        		+ " the purpose of doing eKYC. JPB has informed me that my biometrics will not "
			        		+ "be stored/shared and will be submitted to CIDR only for the purpose of authentication. "
			        		+ "I have reviewed the transaction details and found to be correct. I understand and agree "
			        		+ "to the terms and conditions governing the Service as available on website www.jiobank.in "
			        		+ "and confirm that my biometric authentication be treated as my consent for availing the "
			        		+ "Service from the Bank. I hereby give my consent to receive promotional "
			        		+ "consent on behalf of the Bank. The bank will be responsible for omission and commission of the BC agent";

			        String base64Consent = Base64.getEncoder().encodeToString(consent.getBytes(StandardCharsets.UTF_8));
			        
			        DateTimeFormatter formatter = DateTimeFormatter
			                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
			                .withZone(ZoneOffset.UTC);

			        String timestamp = formatter.format(Instant.now());
			        String saTxn = sa + ":" + timestamp;
			        log.info("SaTxn :: {}", saTxn);
			                
			        if (pidData == null) {
			            throw new RuntimeException("Invalid PID XML: Missing PidData");
			        }

			        JSONObject resp = pidData.optJSONObject("Resp");
			        JSONObject deviceInfo = pidData.optJSONObject("DeviceInfo");
			        JSONObject skeyObj = pidData.optJSONObject("Skey");
			        JSONObject dataObj = pidData.optJSONObject("Data");

			        String hmac = String.valueOf(pidData.opt("Hmac"));

			        //Build JSON
			        JSONObject finalJson = new JSONObject();
			        finalJson.put("type", "1");

			        JSONObject captureResponse = new JSONObject();

			        // Data section
			        captureResponse.put("PidDatatype", String.valueOf(dataObj != null ? dataObj.opt("type") : ""));
			        captureResponse.put("Piddata", String.valueOf(dataObj != null ? dataObj.opt("content") : ""));

			        // Skey section
			        captureResponse.put("ci", String.valueOf(skeyObj != null ? skeyObj.opt("ci") : ""));
			        captureResponse.put("sessionKey", String.valueOf(skeyObj != null ? skeyObj.opt("content") : ""));

			        // HMAC
			        captureResponse.put("hmac", hmac);

			        // Resp section
			        captureResponse.put("errCode", String.valueOf(resp != null ? resp.opt("errCode") : ""));
			        captureResponse.put("errInfo", String.valueOf(resp != null ? resp.opt("errInfo") : ""));
			        captureResponse.put("fCount", String.valueOf(resp != null ? resp.opt("fCount") : ""));
			        captureResponse.put("fType", String.valueOf(resp != null ? resp.opt("fType") : ""));
			        captureResponse.put("nmPoints", String.valueOf(resp != null ? resp.opt("nmPoints") : ""));
			        captureResponse.put("qScore", String.valueOf(resp != null ? resp.opt("qScore") : ""));

			        // Device Info mapping
			        captureResponse.put("dpID", String.valueOf(deviceInfo != null ? deviceInfo.opt("dpId") : ""));
			        captureResponse.put("rdsID", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsId") : ""));
			        captureResponse.put("rdsVer", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsVer") : ""));
			        captureResponse.put("dc", String.valueOf(deviceInfo != null ? deviceInfo.opt("dc") : ""));
			        captureResponse.put("mi", String.valueOf(deviceInfo != null ? deviceInfo.opt("mi") : ""));
			        captureResponse.put("mc", String.valueOf(deviceInfo != null ? deviceInfo.opt("mc") : ""));
			        captureResponse.put("consent", base64Consent);
			        captureResponse.put("rc", "Y");
			        captureResponse.put("ver", "2.0");
			        captureResponse.put("tid", "registered");
			        captureResponse.put("sa", sa);
			        captureResponse.put("saTxn", saTxn);
			        captureResponse.put("appCode", appCode);

			        finalJson.put("captureResponse", captureResponse);

			        // Device Info block
			        JSONObject device = new JSONObject();
			        device.put("dpId", String.valueOf(deviceInfo != null ? deviceInfo.opt("dpId") : ""));
			        device.put("rdsId", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsId") : ""));
			        device.put("rdsVer", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsVer") : ""));
			        device.put("dc", String.valueOf(deviceInfo != null ? deviceInfo.opt("dc") : ""));
			        device.put("mi", String.valueOf(deviceInfo != null ? deviceInfo.opt("mi") : ""));
			        device.put("mc", String.valueOf(deviceInfo != null ? deviceInfo.opt("mc") : ""));

			        finalJson.put("deviceInfo", device);
			        
			        log.info("Bio-metric JSON :: {}", finalJson);

			        //JSON → String
			        String jsonString = finalJson.toString();

			        //Base64 encode
			        String base64Encoded = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

			        return base64Encoded;

			    } catch (Exception e) {
			        e.printStackTrace();
//			        throw new RuntimeException("Error converting XML to Base64 JSON", e);
			        return "Not Proper FingerPrint Data";
			    }
			}

	// Bio metric string to json and base64 conversion
	public String convertPidXmlToBase64Json(String xmlData) {
		    try {
		        //XML → JSON
		        JSONObject xmlJson = XML.toJSONObject(xmlData);
		        JSONObject pidData = xmlJson.optJSONObject("PidData");
		        
		        String consent = "I hereby provide my consent to Jio Payments Bank Limited (Bank) to "
		        		+ "use my Aadhaar number and biometric authentication to verify my identity for"
		        		+ " the purpose of doing eKYC. JPB has informed me that my biometrics will not "
		        		+ "be stored/shared and will be submitted to CIDR only for the purpose of authentication. "
		        		+ "I have reviewed the transaction details and found to be correct. I understand and agree "
		        		+ "to the terms and conditions governing the Service as available on website www.jiobank.in "
		        		+ "and confirm that my biometric authentication be treated as my consent for availing the "
		        		+ "Service from the Bank. I hereby give my consent to receive promotional "
		        		+ "consent on behalf of the Bank. The bank will be responsible for omission and commission of the BC agent";

		        String base64Consent = Base64.getEncoder().encodeToString(consent.getBytes(StandardCharsets.UTF_8));
		        
		        String saTxn = sa + ":" + Instant.now().toString(); 
		                
		        if (pidData == null) {
		            throw new RuntimeException("Invalid PID XML: Missing PidData");
		        }

		        JSONObject resp = pidData.optJSONObject("Resp");
		        JSONObject deviceInfo = pidData.optJSONObject("DeviceInfo");
		        JSONObject skeyObj = pidData.optJSONObject("Skey");
		        JSONObject dataObj = pidData.optJSONObject("Data");

		        String hmac = String.valueOf(pidData.opt("Hmac"));

		        //Build JSON
		        JSONObject finalJson = new JSONObject();
		        finalJson.put("type", "1");

		        JSONObject captureResponse = new JSONObject();

		        // Data section
		        captureResponse.put("PidDatatype", String.valueOf(dataObj != null ? dataObj.opt("type") : ""));
		        captureResponse.put("Piddata", String.valueOf(dataObj != null ? dataObj.opt("content") : ""));

		        // Skey section
		        captureResponse.put("ci", String.valueOf(skeyObj != null ? skeyObj.opt("ci") : ""));
		        captureResponse.put("sessionKey", String.valueOf(skeyObj != null ? skeyObj.opt("content") : ""));

		        // HMAC
		        captureResponse.put("hmac", hmac);

		        // Resp section
		        captureResponse.put("errCode", String.valueOf(resp != null ? resp.opt("errCode") : ""));
		        captureResponse.put("errInfo", String.valueOf(resp != null ? resp.opt("errInfo") : ""));
		        captureResponse.put("fCount", String.valueOf(resp != null ? resp.opt("fCount") : ""));
		        captureResponse.put("fType", String.valueOf(resp != null ? resp.opt("fType") : ""));
		        captureResponse.put("nmPoints", String.valueOf(resp != null ? resp.opt("nmPoints") : ""));
		        captureResponse.put("qScore", String.valueOf(resp != null ? resp.opt("qScore") : ""));

		        // Device Info mapping
		        captureResponse.put("dpID", String.valueOf(deviceInfo != null ? deviceInfo.opt("dpId") : ""));
		        captureResponse.put("rdsID", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsId") : ""));
		        captureResponse.put("rdsVer", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsVer") : ""));
		        captureResponse.put("dc", String.valueOf(deviceInfo != null ? deviceInfo.opt("dc") : ""));
		        captureResponse.put("mi", String.valueOf(deviceInfo != null ? deviceInfo.opt("mi") : ""));
		        captureResponse.put("mc", String.valueOf(deviceInfo != null ? deviceInfo.opt("mc") : ""));
		        captureResponse.put("consent", base64Consent);
		        captureResponse.put("rc", "Y");
		        captureResponse.put("ver", "2.0");
		        captureResponse.put("tid", "registered");
		        captureResponse.put("sa", sa);
		        captureResponse.put("saTxn", saTxn);
		        captureResponse.put("appCode", appCode);

		        finalJson.put("captureResponse", captureResponse);

		        // Device Info block
		        JSONObject device = new JSONObject();
		        device.put("dpId", String.valueOf(deviceInfo != null ? deviceInfo.opt("dpId") : ""));
		        device.put("rdsId", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsId") : ""));
		        device.put("rdsVer", String.valueOf(deviceInfo != null ? deviceInfo.opt("rdsVer") : ""));
		        device.put("dc", String.valueOf(deviceInfo != null ? deviceInfo.opt("dc") : ""));
		        device.put("mi", String.valueOf(deviceInfo != null ? deviceInfo.opt("mi") : ""));
		        device.put("mc", String.valueOf(deviceInfo != null ? deviceInfo.opt("mc") : ""));

		        finalJson.put("deviceInfo", device);
		        
		        log.info("Bio-metric JSON :: {}", finalJson);

		        //JSON → String
		        String jsonString = finalJson.toString();

		        //Base64 encode
		        String base64Encoded = Base64.getEncoder().encodeToString(jsonString.getBytes(StandardCharsets.UTF_8));

		        return base64Encoded;

		    } catch (Exception e) {
		        e.printStackTrace();
		        throw new RuntimeException("Error converting XML to Base64 JSON", e);
		    }
		}
	
	//Send SMS
	public boolean sendSMS(String mobileNo, int otp) {

        try {
        	String jsonData = "{\n"
                    + "    \"mobileNumber\":\"" + mobileNo + "\",\n"
                    + "    \"msg\":\"Your verification code for Registration is " + otp + ". Please do not disclose verification code with anyone. Vakrangee Ltd.\",\n"
                    + "    \"dlt\":\"1007624343981708659\",\n"
                    + "    \"serviceType\":\"implicit\"\n"
                    + "}";
        	
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<String> httpEntity = new HttpEntity<>(jsonData, headers);

            ResponseEntity<String> response = rest.exchange(
            		smsURL,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {

                log.info("SMS sent successfully to mobileNo={}, response={}", mobileNo, response.getBody());
                return true;
            } else {
                log.error("SMS API failed for mobileNo={}, statusCode={}, response={}",mobileNo,response.getStatusCode(),response.getBody());
                return false;
            }

        } catch (HttpClientErrorException | HttpServerErrorException ex) {

            log.error("SMS API error for mobileNo={}, status={}, response={}",
                    mobileNo, ex.getStatusCode(), ex.getResponseBodyAsString(), ex );
            return false;

        } catch (ResourceAccessException ex) {

            log.error("SMS API timeout/connection error for mobileNo={}",mobileNo,ex);
            return false;

        } catch (Exception ex) {
            log.error("Unexpected error while sending SMS to mobileNo={}",mobileNo,ex);
            return false;
        }
    }
	
	//Address Validation for Pan Aadhaar Verify
	Map<String, String> buildAddressLines(CustomerPanAadharVerifyResponseDTO response) {

	    Map<String, String> addressMap = new HashMap<>();

	    try {

	        // Extract Aadhaar Address
	        AddressDTO address = response.getData()
	                .getPersons()
	                .getAadhaar()
	                .getAddress();

	        if (address == null) {

	            addressMap.put("line1", null);
	            addressMap.put("line2", null);
	            addressMap.put("line3", null);

	            return addressMap;
	        }

	        // Extract Fields
	        String houseNumber = safe(address.getHouseNumber());
	        String street = safe(address.getStreet());
	        String landmark = safe(address.getLandmark());
	        String locality = safe(address.getLocality());
	        String postOffice = safe(address.getPostOffice());
	        String aadharLine1 = safe(address.getLine1());

	        /*
	         * ADDRESS FORMATION LOGIC
	         *
	         * line1 = houseNumber || locality || landmark || street || Aadhaar line1
	         *
	         * if houseNumber exists:
	         *      line2 = locality + landmark || street || postOffice
	         *
	         * else if locality exists:
	         *      line2 = landmark || street || postOffice
	         *
	         * else if landmark exists:
	         *      line2 = street || postOffice
	         *
	         * line3 = remaining street/postOffice not already used in line2
	         */

	        // ---------------- LINE 1 ----------------

	        String line1 =
	                !isBlank(houseNumber) ? houseNumber :
	                !isBlank(locality) ? locality :
	                !isBlank(landmark) ? landmark :
	                !isBlank(street) ? street :
	                !isBlank(aadharLine1) ? aadharLine1 :
	                null;

	        // ---------------- LINE 2 ----------------

	        String line2 = null;

	        if (!isBlank(houseNumber)) {

	            line2 = !isBlank(join(locality, landmark))
	                    ? join(locality, landmark)
	                    : !isBlank(street)
	                        ? street
	                        : !isBlank(postOffice)
	                            ? postOffice
	                            : null;

	        } else if (!isBlank(locality)) {

	            line2 = !isBlank(landmark)
	                    ? landmark
	                    : !isBlank(street)
	                        ? street
	                        : !isBlank(postOffice)
	                            ? postOffice
	                            : null;

	        } else if (!isBlank(landmark)) {

	            line2 = !isBlank(street)
	                    ? street
	                    : !isBlank(postOffice)
	                        ? postOffice
	                        : null;
	        }

	        // ---------------- LINE 3 ----------------

	        String line3 = null;

	        if (!isBlank(street) || !isBlank(postOffice)) {

	            boolean usedStreet = street != null && street.equals(line2);
	            boolean usedPostOffice = postOffice != null && postOffice.equals(line2);

	            line3 = join(
	                    usedStreet ? null : street,
	                    usedPostOffice ? null : postOffice
	            );
	        }

	        // Clean Final Values
	        line1 = clean(line1);
	        line2 = clean(line2);
	        line3 = clean(line3);

	        // Set Map
	        addressMap.put("line1", line1);
	        addressMap.put("line2", line2);
	        addressMap.put("line3", line3);

	        log.info("Address Line1 :: {}", line1);
	        log.info("Address Line2 :: {}", line2);
	        log.info("Address Line3 :: {}", line3);

	    } catch (Exception e) {

	        log.error("Error while forming address lines", e);

	        addressMap.put("line1", null);
	        addressMap.put("line2", null);
	        addressMap.put("line3", null);
	    }

	    return addressMap;
	}

	private String join(String... values) {

	    return Arrays.stream(values)
	            .filter(Objects::nonNull)
	            .map(String::trim)
	            .filter(val -> !val.isEmpty())
	            .collect(Collectors.joining(", "));
	}

	private String safe(String value) {
	    return value == null ? null : value.trim();
	}

	private String clean(String value) {

	    if (value == null || value.isBlank()) {
	        return null;
	    }

	    return value
	            .replaceAll(",\\s*,", ",")
	            .replaceAll("^,\\s*", "")
	            .replaceAll(",\\s*$", "")
	            .trim();
	}

	private boolean isBlank(String value) {
	    return value == null || value.isBlank();
	}
	
	//Generate ClientRefID for DMT Transactions
	private long count = 0;
	public String generateRRNAndStan() {
	    try {
	        // Get the current timestamp in seconds since epoch
	        long timestampInSeconds = System.currentTimeMillis() / 1000; // This gives us the seconds part
	        String timestampStr = Long.toString(timestampInSeconds);

	        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)).substring(2, 4); // Last 2 digits of the year
	        String doy = String.format("%03d", Calendar.getInstance().get(Calendar.DAY_OF_YEAR)); // Day of year (3 digits)
	        String hour = String.format("%02d", Calendar.getInstance().get(Calendar.HOUR_OF_DAY)); // Hour of day (2 digits)

	        String rrn = String.format("%s%s%s%s", year, doy, hour, timestampStr.substring(timestampStr.length() - 2));

	        long stanValue = generateStan();
	        String stan = String.format("%06d", stanValue); // Format STAN with leading zeros

	        return rrn + stan;

	    } catch (Exception e) {
	        log.error("Exception generateRRNAndStan :" + e);
	        return "";
	    }
	}
	
	private synchronized long generateStan() {
	    if (++count > 999999) {
	        count = 1;
	    }
	    return count;
	}

}
