package com.jpb.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jpb.DTO.CustomerInputRequestDTO;
import com.jpb.Service.RefundService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jpb/refund")
@CrossOrigin("*")
public class RefundController {

	@Autowired
	RefundService service;
	
	@PostMapping("/voucher-verify")
	public ResponseEntity<?> voucherVerify(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.voucherVerify(request, httpRequest);
	}
	
	@PostMapping("/voucher-redeem")
	public ResponseEntity<?> voucherRedeem(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.voucherRedeem(request, httpRequest);
	}
	
	@PostMapping("/voucher-resend")
	public ResponseEntity<?> voucherResend(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.voucherResend(request, httpRequest);
	}
	
	@PostMapping("/customer-resend-voucher")
	public ResponseEntity<?> customerVoucherResend(@RequestBody CustomerInputRequestDTO request, HttpServletRequest httpRequest){
		return service.customerVoucherResend(request, httpRequest);
	}
}
