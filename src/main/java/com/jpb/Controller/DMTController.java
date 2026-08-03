package com.jpb.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpb.DTO.DmtCommonrequestDto;
import com.jpb.Service.DMTService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jpb/DMT")
@CrossOrigin("*")
public class DMTController {

	@Autowired
	DMTService service;
	
	@PostMapping("/check-mobileNo")
	public ResponseEntity<?> checkMobileNo(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest) {
		return service.checkmobileNo(request, httpRequest);		
	}
	
	@PostMapping("/generate-OTP")
	public ResponseEntity<?> generateOtp(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.generateDmtOtp(request, httpRequest);
	}	
	
	@PostMapping("/verify-OTP")
	public ResponseEntity<?> verifyOtp(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.verifyDmtOtp(request, httpRequest);
	}	
	
	@PostMapping("/EKYC")
	public ResponseEntity<?> dmtEkyc(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.dmtEkyc(request, httpRequest);
	}	
	
	@PostMapping("/register-user")
	public ResponseEntity<?> registerUser(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.registerUser(request, httpRequest);
	}
	
	@PostMapping("/transaction")
	public ResponseEntity<?> tranx(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.tranx(request, httpRequest);
	}
	
	@PostMapping("/transaction-status-check")
	public ResponseEntity<?> tranxStatusCheck(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.tranxStatusCheck(request, httpRequest);
	}
	
	@PostMapping("/transaction-history")
	public ResponseEntity<?> tranxHistory(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.tranxHistory(request, httpRequest);
	}
	
	@PostMapping("/customer-limit")
	public ResponseEntity<?> customerLimit(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.customerLimit(request, httpRequest);
	}
	
	@PostMapping("/bene-validation")
	public ResponseEntity<?> beneValidation(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.beneValidation(request, httpRequest);
	}
	
	@PostMapping("/add-beneficiary")
	public ResponseEntity<?> addBene(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.addBene(request, httpRequest);
	}
	
	@PostMapping("/customer-details")
	public ResponseEntity<?> custDetails(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.custDetails(request, httpRequest);
	}
	
	@PostMapping("/amount-calculation")
	public ResponseEntity<?> amtCalculation(@RequestBody DmtCommonrequestDto request,HttpServletRequest httpRequest ) {
		return service.amtCalculation(request, httpRequest);
	}
}
