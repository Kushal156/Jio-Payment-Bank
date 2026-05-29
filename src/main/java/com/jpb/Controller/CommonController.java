package com.jpb.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpb.DTO.CustomerInputRequestDTO;
import com.jpb.ServiceImpl.SchedulerServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@RestController
@RequestMapping("/common")
@Slf4j
@CrossOrigin("*")
public class CommonController {
	
	@Autowired
	SchedulerServiceImpl service;
	
	@Autowired
    private CacheManager cacheManager;

	@PostMapping("/account-sub")
	public ResponseEntity<?> accountSubs(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.AccountSubscription(request, httpRequest);
	}
	
	@PostMapping("/get-consents")
	public ResponseEntity<?> getConsents(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.getConsents(request, httpRequest);
	}
	
	@PostMapping("/pin-code")
	public ResponseEntity<?> getPinDetails(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.getPinDetails(request, httpRequest);
	}
	
	@PostMapping("/agent-otp")
	public ResponseEntity<?> agentOTP(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.agentOTP(request, httpRequest);
	}
	
	@PostMapping("/verify-agent-otp")
	public ResponseEntity<?> verifyAgentOTP(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.verifyAgentOTP(request, httpRequest);
	}
	
	@GetMapping("/check-cache")
	public String checkCache() {

	    Cache subs = cacheManager.getCache("accountSubscriptionCache");
	    Cache consents = cacheManager.getCache("");
	    
	    Object subsValue = subs.get("SUBSCRIPTION");
	    Object consentsValue = consents.get("CONSENTS");
	    
	    log.info("account subs cache :: {}", subsValue.toString());
	    log.info("consents cache :: {}", consentsValue.toString());
	   
	    return (subsValue != null && consentsValue != null) ? "CACHE PRESENT" : "CACHE EMPTY";
	}
}
