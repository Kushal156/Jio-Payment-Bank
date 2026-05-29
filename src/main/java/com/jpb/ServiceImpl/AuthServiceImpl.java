package com.jpb.ServiceImpl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.jpb.Config.TokenManager;
import com.jpb.DTO.ApplicationDTO;
import com.jpb.DTO.AuthTokenRequestDTO;
import com.jpb.DTO.AuthTokenResponseDTO;
import com.jpb.DTO.AuthenticateDTO;
import com.jpb.DTO.HeaderDeviceInfoDTO;
import com.jpb.DTO.SecureDTO;
import com.jpb.Service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

	@Value("${tokenURL}")
	private String tokenURL;
	
	@Value("${channelID}")
	private String channelId;
	
	@Value("${clientID}")
	private String clientId;
	
	@Value("${AppName}")
	private String appName;
	
	@Value("${SecretCode}")
	private String SecretCode;
	
	@Value("${purpose}")
	private Integer purpose;
	
	@Value("${PublicKeyPath}")
	private String PublicKeyPath;
	
	@Autowired
	RestTemplate rest;
	
	@Autowired
	EncryptionService encrypt;
	
	@Autowired
	TokenManager tokenManager;

	@Override
	public ResponseEntity<AuthTokenResponseDTO> generateToken(HttpServletRequest httpRequest) {

		log.info("httpRequest Header Request :: {}", httpRequest.toString());
		ObjectMapper mapper = new ObjectMapper();
		HeaderDeviceInfoDTO deviceInfo = new HeaderDeviceInfoDTO();
		try {

			String userAgent = httpRequest.getHeader("User-Agent");
			String ipAddress = httpRequest.getRemoteAddr();

			String deviceType = "WEB";
			String os = "UNKNOWN";

			if (userAgent != null) {

			    String ua = userAgent.toLowerCase();

			    //Detect Mobile
			    if (ua.contains("android") || ua.contains("iphone") || ua.contains("mobile")) {
			        deviceType = "MOB";
			    }

			    //Detect OS
			    if (ua.contains("android")) {
			        os = "ANDROID";
			    } else if (ua.contains("iphone") || ua.contains("ios")) {
			        os = "IOS";
			    } else if (ua.contains("windows")) {
			        os = "WINDOWS";
			    } else if (ua.contains("mac")) {
			        os = "MAC";
			    } else if (ua.contains("linux")) {
			        os = "LINUX";
			    }
			}

			deviceInfo.setIpAddress(ipAddress);
			deviceInfo.setType(deviceType);
			deviceInfo.setOs(os); 
			deviceInfo.setAppName(appName);
			deviceInfo.setAppId("APP001");
			deviceInfo.setSdkVersion("1.0");
			deviceInfo.setMobile("9999999999");
			deviceInfo.setUserAgent(userAgent);

			String deviceInfoJson = mapper.writeValueAsString(deviceInfo);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("X-DEVICE-INFO", deviceInfoJson);
			headers.set("x-channel-id", channelId);
			headers.set("x-trace-id", UUID.randomUUID().toString());
			
			//request formation
			AuthTokenRequestDTO request = new AuthTokenRequestDTO();
			
			ApplicationDTO applicationDto = new ApplicationDTO();
			applicationDto.setApplicationName("Jio Payment Bank");
			applicationDto.setClientId(clientId);
			
			AuthenticateDTO authDTO = new AuthenticateDTO();
			String aesKey = encrypt.generateRandomString(16);
			authDTO.setMode(20);
			authDTO.setValue(encrypt.encryptAES(SecretCode, aesKey));
			
			request.setPurpose(purpose);
			request.setScope("SESSION");
			
			SecureDTO secure = new SecureDTO();
			String base64PublicKey = new String(Files.readAllBytes(Paths.get(PublicKeyPath)));
			secure.setEncryptionKey(encrypt.encryptRSA(aesKey, base64PublicKey));
			
			request.setApplication(applicationDto);
			List<AuthenticateDTO> authList = new ArrayList<>();
			authList.add(authDTO);

			request.setAuthenticateList(authList);
			request.setSecure(secure);
			
			log.info("Final Request formation for token :: {}", request.toString());
			log.info("JSON Request for token Generation :: {}", mapper.writeValueAsString(request));
			
			HttpEntity<AuthTokenRequestDTO> entity = new HttpEntity<>(request, headers);
			
			ResponseEntity<AuthTokenResponseDTO> response =
	                rest.exchange(
	                        tokenURL,
	                        HttpMethod.POST,
	                        entity,
	                        AuthTokenResponseDTO.class
	                );

	        log.info("Token API Response :: {}", response.getBody());
	        
	        if (response.getBody() != null && "SUCCESS".equalsIgnoreCase(response.getBody().getStatus())) {
	            tokenManager.saveToken(response.getBody());
	        }

	        return response;

		} catch (Exception e) {
			 log.error("Error while generating token", e);
		        throw new RuntimeException("Token generation failed", e);
		}
	}

}
