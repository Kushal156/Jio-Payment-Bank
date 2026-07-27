package com.jpb.Controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.jpb.DTO.AepsCommonRequestDto;
import com.jpb.DTO.JpbAepsResponseDto;
import com.jpb.Service.JpbAepsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/jpb/AEPS")
public class JpbAepsController {

	@Autowired
	private JpbAepsService jpbAepsService;
	
	@PostMapping("/cashDeposit")
	public JpbAepsResponseDto CashDeposite(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest){
		JpbAepsResponseDto response = jpbAepsService.CashDeposite(request,  httpRequest);
		return response;
	}
	
	@PostMapping("/aepscashwithdrawal")
	public JpbAepsResponseDto CashWithdrawal(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest){
		JpbAepsResponseDto	response = jpbAepsService.AepsCashWithdrawal(request,  httpRequest);
		return response;
	}
	
	@PostMapping("/aepsFundTransfer")
	public JpbAepsResponseDto fundTransfer(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest) {
		JpbAepsResponseDto response = jpbAepsService.AepsFundTransfer(request,httpRequest);
		return response;	  
	}
	
	@PostMapping("/aepsMiniStatement")
	public JpbAepsResponseDto miniStatement(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest ) {     
		JpbAepsResponseDto response = jpbAepsService.AepsMiniStement(request,httpRequest);
		return response;
	}	
	
	@PostMapping("/balanceInquiry")
	public JpbAepsResponseDto BalanceInquiry(@RequestBody AepsCommonRequestDto request, HttpServletRequest httpRequest) {
		JpbAepsResponseDto response = jpbAepsService.BalanceInquiry(request,httpRequest);
		return response;
		
	}
	
	@PostMapping("/agentHistory")
	public JpbAepsResponseDto agentHistory(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest) {
		JpbAepsResponseDto response = jpbAepsService.AgentHistory(request,httpRequest);
		return response;
	}
	
	@PostMapping("/agentInfo")
	public JpbAepsResponseDto agentInfo(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest) {
		JpbAepsResponseDto response = jpbAepsService.AgentInfo(request, httpRequest);
		return response;
	}
	
	@PostMapping("/cashDepositegenerateotp")
	public JpbAepsResponseDto cashWithdrawalGenerateOtp(@RequestBody AepsCommonRequestDto request,HttpServletRequest httpRequest) {
		JpbAepsResponseDto response = jpbAepsService.CashdepositeGenerateOtp(request, httpRequest);
		return response;
	}
	
}
